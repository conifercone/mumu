/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.basis.enums;

/**
 * Token声言
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SuppressWarnings("LombokGetterMayBeUsed")
public enum TokenClaimsEnum {

  /**
   * 权限
   */
  AUTHORITIES("ats"),

  /**
   * 账户ID
   */
  ACCOUNT_ID("aid"),

  /**
   * 账户名
   */
  ACCOUNT_NAME("ane"),

  /**
   * 时区
   */
  TIMEZONE("tz"),

  /**
   * 语言偏好
   */
  LANGUAGE("lang"),

  /**
   * 授权类型
   */
  AUTHORIZATION_GRANT_TYPE("grant");


  private final String claimName;

  public String getClaimName() {
    return claimName;
  }

  TokenClaimsEnum(String claimName) {
    this.claimName = claimName;
  }
}
