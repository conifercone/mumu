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
    val ip = request.remoteAddr ?: return ""
    return if (ip.startsWith("[") && ip.endsWith("]")) ip.substring(1, ip.length - 1) else ip
  }

}
