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

import jakarta.servlet.http.HttpServletRequest
import java.util.concurrent.atomic.AtomicReference

/**
 * ip工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
object IpUtils {
    /**
     * 从request对象中获取客户端真实的ip地址
     *
     * @param request request对象
     * @return 客户端的IP地址
     */
    @JvmStatic
    fun getIpAddr(request: HttpServletRequest): String? {
        val ip = AtomicReference(request.remoteAddr)
        @Suppress("SpellCheckingInspection") val localIp = setOf(
            "127.0.0.1",      // loopback address (IPv4)
            "::1",            // loopback address (IPv6)
            "localhost",      // equivalent to 127.0.0.1
            "unknown"         // used when the IP is not available
        )

        // 如果 IP 是本地地址或 "unknown"，则不使用
        if (localIp.contains(ip.get())) {
            ip.set(null)
        }

        // 按照可信度从高到低的顺序处理请求头
        val headers = listOf(
            "X-Real-IP",               // 最高可信度
            "X-Forwarded-For",         // 次高可信度
            "Proxy-Client-IP",         // 相对较低的可信度
            "WL-Proxy-Client-IP",      // 相对较低的可信度
            "HTTP_CLIENT_IP",          // 可能包含客户端 IP 地址
            "HTTP_X_FORWARDED_FOR"     // 可能包含客户端 IP 地址
        )

        var foundIp = false  // 标志位，表示是否已找到有效的 IP 地址

        headers.forEach { header ->
            if (!foundIp) {
                val headerValue = request.getHeader(header)
                if (!headerValue.isNullOrEmpty() && !"unknown".equals(
                        headerValue,
                        ignoreCase = true
                    )
                ) {
                    if (header == "X-Forwarded-For") {
                        // 对 X-Forwarded-For 进行处理，获取第一个有效的 IP
                        headerValue.split(",").forEach { ipCandidate ->
                            if (!"unknown".equals(ipCandidate.trim(), ignoreCase = true)) {
                                ip.set(ipCandidate.trim())
                                foundIp = true  // 找到有效 IP 后标记
                                return@forEach
                            }
                        }
                    } else {
                        ip.set(headerValue)
                        foundIp = true  // 找到有效 IP 后标记
                    }
                }
            }
        }

        return ip.get()
    }

}
