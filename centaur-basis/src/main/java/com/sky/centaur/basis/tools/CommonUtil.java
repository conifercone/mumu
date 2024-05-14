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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * 通用工具类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class CommonUtil {

  private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

  /**
   * 校验邮箱地址格式的方法
   * @param email 邮箱地址
   * @return 是否为合法格式的邮箱地址
   */
  @API(status = Status.STABLE, since = "1.0.0")
  public static boolean isValidEmailFormat(String email) {
    if (email == null) {
      return false;
    }
    Matcher matcher = EMAIL_PATTERN.matcher(email);
    return matcher.matches();
  }

  /**
   * 校验邮箱有效性
   *
   * @param email 邮箱地址
   * @return 是否为有效邮箱地址
   */
  @API(status = Status.STABLE, since = "1.0.0")
  public static boolean isValidEmail(String email) {
    if (!isValidEmailFormat(email)) {
      return false;
    }
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
    } catch (AddressException ex) {
      return false;
    }
    return true;
  }

}
