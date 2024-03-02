/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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

package com.sky.centaur.basis.tools;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * ip工具类
 *
 * @author 单开宇
 * @since 2024-02-22
 */
public final class IpUtil {

  /**
   * 从request对象中获取客户端真实的ip地址
   *
   * @param request request对象
   * @return 客户端的IP地址
   */
  public static String getIpAddr(@NotNull HttpServletRequest request) {
    String ip = request.getRemoteAddr();
    ArrayList<String> localIp = new ArrayList<>();
    localIp.add("127.0.0.1");
    localIp.add("192.168.1.110");
    localIp.add("unknown");
    if (localIp.contains(ip)) {
      ip = null;
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("x-forwarded-for");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }

    return ip;
  }
}
