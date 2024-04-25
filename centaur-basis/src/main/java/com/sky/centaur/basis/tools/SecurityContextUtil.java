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

import com.sky.centaur.basis.enums.TokenClaimsEnum;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.ClaimAccessor;

/**
 * 认证上下文工具类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class SecurityContextUtil {

  /**
   * Account领域模型id属性名
   */
  private static final String ID = "id";

  public static @Nullable Long getLoginAccountId() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(authentication -> {
          if (authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
              BeanMap beanMap = BeanMap.create(principal);
              if (beanMap.containsKey(ID)) {
                return Long.parseLong(String.valueOf(beanMap.get(ID)));
              }
              return null;
            } else if (principal instanceof ClaimAccessor claimAccessor) {
              Map<String, Object> claims = claimAccessor.getClaims();
              return Long.parseLong(String.valueOf(claims.get(TokenClaimsEnum.ACCOUNT_ID.name())));
            }
            return null;
          }
          return null;
        }).orElse(null);
  }
}
