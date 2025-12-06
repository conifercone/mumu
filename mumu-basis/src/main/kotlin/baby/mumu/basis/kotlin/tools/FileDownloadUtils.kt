/*
 * Copyright (c) 2024-2025, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package baby.mumu.basis.kotlin.tools

import baby.mumu.basis.exception.ApplicationException
import baby.mumu.basis.response.ResponseCode
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.apiguardian.api.API
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.Assert
import tools.jackson.core.JsonEncoding
import tools.jackson.datatype.javax.money.JavaxMoneyModule
import tools.jackson.module.kotlin.jsonMapper
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.StructuredTaskScope
import java.util.stream.Stream


/**
 * 文件下载工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
object FileDownloadUtils {

  /**
   * 通用文件下载工具（字节数组版本）
   *
   * @param response    HttpServletResponse
   * @param fileName    下载的文件名
   * @param fileContent 文件内容字节数组
   * @param contentType 文件类型 (如 "application/pdf", "text/csv")
   */
  @Suppress("unused")
  @JvmStatic
  @API(status = API.Status.STABLE, since = "2.4.0")
  fun download(
    response: HttpServletResponse,
    fileName: String,
    fileContent: ByteArray,
    contentType: String = MediaType.APPLICATION_OCTET_STREAM_VALUE
  ) {
    try {
      // 设置响应头
      response.contentType = contentType
      response.characterEncoding = StandardCharsets.UTF_8.name()
      response.setHeader(
        "Content-Disposition",
        "attachment; filename=${encodeFileName(fileName)}"
      )

      // 写入文件内容到响应流
      response.outputStream.use { os ->
        os.write(fileContent)
        os.flush()
      }
    } catch (e: Exception) {
      throw ApplicationException(ResponseCode.FILE_DOWNLOAD_FAILED, e)
    }
  }

  /**
   * 通用文件下载工具（流式下载版本）
   *
   * @param response    HttpServletResponse
   * @param fileName    下载的文件名
   * @param inputStream 文件输入流
   * @param contentType 文件类型 (如 "application/pdf", "text/csv")
   */
  @JvmStatic
  @API(status = API.Status.STABLE, since = "2.4.0")
  fun download(
    response: HttpServletResponse,
    fileName: String,
    inputStream: InputStream,
    contentType: String = MediaType.APPLICATION_OCTET_STREAM_VALUE
  ) {
    try {
      // 设置响应头
      response.contentType = contentType
      response.characterEncoding = StandardCharsets.UTF_8.name()
      response.setHeader(
        "Content-Disposition",
        "attachment; filename=${encodeFileName(fileName)}"
      )

      // 写入文件内容到响应流
      response.outputStream.use { os ->
        inputStream.use { input ->
          input.copyTo(os)
        }
        os.flush()
      }
    } catch (e: Exception) {
      throw ApplicationException(ResponseCode.FILE_DOWNLOAD_FAILED, e)
    }
  }

  @JvmStatic
  @API(status = API.Status.STABLE, since = "2.4.0")
  fun <T : Any> downloadCSV(
    response: HttpServletResponse,
    fileName: String,
    data: Stream<T>
  ) {
    try {
      Assert.isTrue(StringUtils.isNotBlank(fileName), "fileName must not be blank")
      val newFileName: String = if (!fileName.endsWith(".csv")) {
        "$fileName.csv"
      } else {
        fileName
      }
      // 设置响应头
      response.contentType = "text/csv"
      response.characterEncoding = StandardCharsets.UTF_8.name()
      response.setHeader(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=${encodeFileName(newFileName)}"
      )

      // 使用 OpenCSV 写入 CSV 内容
      OutputStreamWriter(response.outputStream, StandardCharsets.UTF_8).use { writer ->
        val beanToCsv = StatefulBeanToCsvBuilder<T>(writer)
          .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
          .withOrderedResults(true)
          .build()

        // JDK 25：使用新的 StructuredTaskScope API
        StructuredTaskScope.open<Void>().use { scope ->
          var index = 0
          data.forEach { record ->
            if (index == 0) {
              // 第一条在当前线程写，避免 scope 还没完全建立时就 fork
              beanToCsv.write(record)
              @Suppress("AssignedValueIsNeverRead")
              index = 1
            } else {
              // 之后的记录丢到虚拟线程里
              scope.fork<Void>(Runnable {
                beanToCsv.write(record)
              })
            }
          }

          // 等待所有子任务结束：
          // - 全部成功：join() 正常返回
          // - 任意一个失败：join() 抛 StructuredTaskScope.FailedException
          scope.join()
        }
      }
    } catch (e: Exception) {
      throw ApplicationException(ResponseCode.FILE_DOWNLOAD_FAILED, e)
    }
  }

  @JvmStatic
  @API(status = API.Status.STABLE, since = "2.7.0")
  fun <T> downloadJson(
    response: HttpServletResponse,
    fileName: String,
    stream: Stream<T>
  ) {
    var processedFileName = fileName
    try {
      Assert.isTrue(StringUtils.isNotBlank(processedFileName), "fileName must not be blank")
      if (!processedFileName.endsWith(".json")) {
        processedFileName += ".json"
      }
      // 创建 ObjectMapper 实例
      val objectMapper = jsonMapper {
        addModule(JavaxMoneyModule())
      }

      // 创建 ObjectWriter 实例
      val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

      // 获取 JSON 生成器
      val generator = objectMapper
        .createGenerator(response.outputStream, JsonEncoding.UTF8)

      // 设置响应头
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.setHeader(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + encodeFileName(processedFileName)
      )

      // 写入 JSON 数组开始符
      generator.writeStartArray()

      // 遍历流并写入 JSON 对象
      stream.forEach { obj: T ->
        try {
          objectWriter.writeValue(generator, obj)
        } catch (_: IOException) {
          throw ApplicationException(ResponseCode.FILE_DOWNLOAD_FAILED)
        }
      }
      // 写入 JSON 数组结束符
      generator.writeEndArray()
      // 关闭生成器
      generator.close()
    } catch (_: java.lang.Exception) {
      throw ApplicationException(ResponseCode.FILE_DOWNLOAD_FAILED)
    }
  }

  /**
   * 处理文件名编码，避免中文或特殊字符导致文件名下载异常
   *
   * @param fileName 文件名
   * @return 编码后的文件名
   */
  private fun encodeFileName(fileName: String): String =
    URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20")
}
