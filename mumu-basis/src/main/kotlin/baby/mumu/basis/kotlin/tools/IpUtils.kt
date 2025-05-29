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

/**
 * ip工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
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
        var ip: String? = request.remoteAddr

        val localIp = setOf(
            "127.0.0.1",  // 本地回环地址 (IPv4)
            "::1",        // 本地回环地址 (IPv6)
            "localhost",  // 本地主机名
            "unknown"     // 未知地址
        )

        // 如果 IP 是本地地址或 "unknown"，则置空
        if (ip in localIp) {
            ip = null
        }

        // 按可信度排序的请求头列表
        val headers = listOf(
            "X-Real-IP",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        )

        for (header in headers) {
            val headerValue = request.getHeader(header)
            if (!headerValue.isNullOrEmpty() && !"unknown".equals(headerValue, ignoreCase = true)) {
                ip = if (header == "X-Forwarded-For") {
                    // 处理多个 IP，获取第一个有效的 IP
                    headerValue.split(",").map { it.trim() }
                        .firstOrNull { !"unknown".equals(it, ignoreCase = true) }
                } else {
                    headerValue
                }
                if (ip != null) break // 找到有效 IP 就终止循环
            }
        }

        return ip
    }


}
