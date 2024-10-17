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

import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.authority.gateway.AuthorityGateway;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 查询权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AuthorityFindAllCmdExe")
public class AuthorityFindAllCmdExe {

  private final AuthorityGateway authorityGateway;
  private final AuthorityConvertor authorityConvertor;

  @Autowired
  public AuthorityFindAllCmdExe(AuthorityGateway authorityGateway,
      AuthorityConvertor authorityConvertor) {
    this.authorityGateway = authorityGateway;
    this.authorityConvertor = authorityConvertor;
  }

  public Page<AuthorityFindAllCo> execute(AuthorityFindAllCmd authorityFindAllCmd) {
    Assert.notNull(authorityFindAllCmd, "AuthorityFindAllCmd cannot be null");
    Authority authority = authorityConvertor.toEntity(
            authorityFindAllCmd.getAuthorityFindAllQueryCo())
        .orElseGet(Authority::new);
    Page<Authority> authorities = authorityGateway.findAll(authority,
        authorityFindAllCmd.getCurrent(), authorityFindAllCmd.getPageSize());
    List<AuthorityFindAllCo> authorityFindAllCoList = authorities.getContent().stream()
        .map(authorityConvertor::toFindAllCo)
        .filter(Optional::isPresent).map(Optional::get).toList();
    return new PageImpl<>(authorityFindAllCoList, authorities.getPageable(),
        authorities.getTotalElements());
  }
}
