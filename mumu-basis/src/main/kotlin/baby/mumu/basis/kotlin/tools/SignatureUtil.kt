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

import org.slf4j.LoggerFactory
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 签名工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
object SignatureUtil {

    private val logger = LoggerFactory.getLogger(SignatureUtil::class.java)

    @JvmStatic
    fun generateSignature(
        data: String,
        secretKey: String,
        algorithm: String = "HmacSHA256"
    ): ByteArray {
        val mac = Mac.getInstance(algorithm)
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), algorithm)
        mac.init(secretKeySpec)
        return mac.doFinal(data.toByteArray())
    }

    @JvmStatic
    fun validateSignature(
        data: String,
        signature: String,
        secretKey: String,
        algorithm: String = "HmacSHA256"
    ): Boolean {
        try {// 使用相同的数据和密钥生成签名
            val generatedSignature = generateSignature(data, secretKey, algorithm)
            // 将生成的签名转换为十六进制字符串
            val generatedSignatureHex = generatedSignature.toHexString()
            // 比较生成的签名和传入的签名
            return generatedSignatureHex == signature
        } catch (e: Exception) {
            logger.error(e.message, e)
            return false
        }
    }

    // 扩展函数，将字节数组转换为十六进制字符串
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}
