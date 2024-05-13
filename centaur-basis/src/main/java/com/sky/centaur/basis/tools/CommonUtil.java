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

import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * 通用工具类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class CommonUtil {

  /**
   * 邮箱正则表达式
   */
  private static final Pattern EMAIL_ENGLISH_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,7}$");


  /**
   * 是否为合法邮箱格式
   *
   * @param email 邮箱地址
   * @return 结果
   */
  @API(status = Status.STABLE, since = "1.0.0")
  public static boolean isEmail(final String email) {
    return EMAIL_ENGLISH_PATTERN.matcher(email).matches();
  }

}
