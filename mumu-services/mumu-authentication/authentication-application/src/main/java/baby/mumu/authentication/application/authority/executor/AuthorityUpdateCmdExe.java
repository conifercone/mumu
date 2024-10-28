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
package baby.mumu.authentication.application.authority.executor;

import baby.mumu.authentication.client.dto.AuthorityUpdateCmd;
import baby.mumu.authentication.domain.authority.gateway.AuthorityGateway;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 更新权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AuthorityUpdateCmdExe")
public class AuthorityUpdateCmdExe {

  private final AuthorityGateway authorityGateway;
  private final AuthorityConvertor authorityConvertor;

  @Autowired
  public AuthorityUpdateCmdExe(AuthorityGateway authorityGateway,
    AuthorityConvertor authorityConvertor) {
    this.authorityGateway = authorityGateway;
    this.authorityConvertor = authorityConvertor;
  }

  public void execute(@NotNull AuthorityUpdateCmd authorityUpdateCmd) {
    Assert.notNull(authorityUpdateCmd, "AuthorityUpdateCmd cannot be null");
    authorityConvertor.toEntity(authorityUpdateCmd.getAuthorityUpdateCo())
      .ifPresent(authorityGateway::updateById);
  }
}
