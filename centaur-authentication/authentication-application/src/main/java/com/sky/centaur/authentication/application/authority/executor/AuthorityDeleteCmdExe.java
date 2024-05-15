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

package com.sky.centaur.authentication.application.authority.executor;

import com.sky.centaur.authentication.client.dto.AuthorityDeleteCmd;
import com.sky.centaur.authentication.client.dto.co.AuthorityDeleteCo;
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.domain.authority.gateway.AuthorityGateway;
import com.sky.centaur.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除权限指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "AuthorityDeleteCmdExe")
public class AuthorityDeleteCmdExe {

  private final AuthorityGateway authorityGateway;

  @Autowired
  public AuthorityDeleteCmdExe(AuthorityGateway authorityGateway) {
    this.authorityGateway = authorityGateway;
  }

  public AuthorityDeleteCo execute(@NotNull AuthorityDeleteCmd authorityDeleteCmd) {
    Authority authority = AuthorityConvertor.toEntity(authorityDeleteCmd.getAuthorityDeleteCo());
    authorityGateway.delete(authority);
    return authorityDeleteCmd.getAuthorityDeleteCo();
  }
}
