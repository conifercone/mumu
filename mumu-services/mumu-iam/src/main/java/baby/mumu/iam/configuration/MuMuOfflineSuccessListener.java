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

package baby.mumu.iam.configuration;

import baby.mumu.basis.event.OfflineSuccessEvent;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

/**
 * 用户下线成功监听
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Component
public class MuMuOfflineSuccessListener {

  private final OAuth2AuthorizationService oAuth2AuthorizationService;

  @Autowired
  public MuMuOfflineSuccessListener(OAuth2AuthorizationService oAuth2AuthorizationService) {
    this.oAuth2AuthorizationService = oAuth2AuthorizationService;
  }

  @EventListener
  public void onOfflineSuccess(@NonNull OfflineSuccessEvent event) {
    String tokenValue = event.getTokenValue();
    if (StringUtils.isNotBlank(tokenValue)) {
      Optional.ofNullable(oAuth2AuthorizationService.findByToken(tokenValue,
        OAuth2TokenType.ACCESS_TOKEN)).ifPresent(oAuth2AuthorizationService::remove);
      Optional.ofNullable(oAuth2AuthorizationService.findByToken(tokenValue,
        OAuth2TokenType.REFRESH_TOKEN)).ifPresent(oAuth2AuthorizationService::remove);
    }
  }
}
