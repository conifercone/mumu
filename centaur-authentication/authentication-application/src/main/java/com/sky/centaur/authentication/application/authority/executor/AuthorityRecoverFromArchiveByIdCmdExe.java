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

import com.sky.centaur.authentication.client.dto.AuthorityRecoverFromArchiveByIdCmd;
import com.sky.centaur.authentication.domain.authority.gateway.AuthorityGateway;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 根据id从归档中恢复权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
@Component
@Observed(name = "AuthorityRecoverFromArchiveByIdCmdExe")
public class AuthorityRecoverFromArchiveByIdCmdExe {

  private final AuthorityGateway authorityGateway;

  @Autowired
  public AuthorityRecoverFromArchiveByIdCmdExe(AuthorityGateway authorityGateway) {
    this.authorityGateway = authorityGateway;
  }

  public void execute(AuthorityRecoverFromArchiveByIdCmd authorityRecoverFromArchiveByIdCmd) {
    Optional.ofNullable(authorityRecoverFromArchiveByIdCmd)
        .ifPresent(recoverFromArchiveByIdCmd -> authorityGateway.recoverFromArchiveById(
            recoverFromArchiveByIdCmd.getId()));
  }
}
