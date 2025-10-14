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

package baby.mumu.extension.provider;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.provider.RateLimitingKeyProvider;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * grpc ip实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
public class RateLimitingGrpcIpKeyProviderImpl implements RateLimitingKeyProvider {

  @Override
  public String generateUniqKey() {
    return Optional.ofNullable(ClientIpInterceptor.getClientIp())
      .filter(StringUtils::isNotBlank)
      .orElseThrow(
        () -> new ApplicationException(ResponseCode.UNABLE_TO_OBTAIN_CURRENT_REQUESTED_IP));
  }
}
