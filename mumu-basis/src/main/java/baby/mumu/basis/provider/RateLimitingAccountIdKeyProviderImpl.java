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
package baby.mumu.basis.provider;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.basis.response.ResponseCode;

/**
 * 账户ID实现
 * <p>使用此限流ID需要注意方法是否需要鉴权才能访问，如果不需要鉴权就可以访问那么此处会抛出UNAUTHORIZED</p>
 * <p>所以使用此种限流ID时需要保证该限流ID对应的方法是需要鉴权才能访问的方法</p>
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
public class RateLimitingAccountIdKeyProviderImpl implements RateLimitingKeyProvider {

  @Override
  public String generateUniqKey() {
    return String.valueOf(SecurityContextUtils.getLoginAccountId()
      .orElseThrow(() -> new MuMuException(
        ResponseCode.FAILURE_TO_GET_INFORMATION_RELATED_TO_THE_LOGIN_ACCOUNT)));
  }
}
