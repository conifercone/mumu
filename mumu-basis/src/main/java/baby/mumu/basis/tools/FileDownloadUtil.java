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
package baby.mumu.basis.tools;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.zalando.jackson.datatype.money.MoneyModule;

/**
 * 文件下载工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.6.0
 */
@SuppressWarnings("unused")
public final class FileDownloadUtil {

  private FileDownloadUtil() {
  }

  @API(status = API.Status.STABLE, since = "2.6.0")
  public static <T> void downloadJson(
    HttpServletResponse response,
    String fileName,
    Stream<T> stream
  ) {
    try {
      // 创建 ObjectMapper 实例
      ObjectMapper objectMapper = new ObjectMapper();
      // 注册模块（例如时间模块和Money模块）
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.registerModule(new MoneyModule());

      // 创建 ObjectWriter 实例
      ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

      // 获取 JSON 生成器
      JsonGenerator generator = objectMapper.getFactory()
        .createGenerator(response.getOutputStream());
      generator.useDefaultPrettyPrinter();

      // 设置响应头
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + encodeFileName(fileName));

      // 写入 JSON 数组开始符
      generator.writeStartArray();

      // 遍历流并写入 JSON 对象
      stream.forEach(obj -> {
        try {
          objectWriter.writeValue(generator, obj);
        } catch (IOException e) {
          throw new MuMuException(ResponseCode.FILE_DOWNLOAD_FAILED);
        }
      });
      // 写入 JSON 数组结束符
      generator.writeEndArray();
      // 关闭生成器
      generator.close();
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FILE_DOWNLOAD_FAILED);
    }
  }

  // 文件名编码方法示例，确保兼容文件名字符集
  @Contract("null -> fail")
  private static @NotNull String encodeFileName(String fileName) {
    // 对文件名进行处理（如果需要）
    assert fileName != null;
    return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
