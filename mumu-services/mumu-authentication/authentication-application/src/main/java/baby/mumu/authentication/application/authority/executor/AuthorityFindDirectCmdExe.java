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

import baby.mumu.authentication.client.dto.AuthorityFindDirectCmd;
import baby.mumu.authentication.client.dto.co.AuthorityFindDirectCo;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.authority.gateway.AuthorityGateway;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * 获取直系后代根权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
@Component
@Observed(name = "AuthorityFindDirectCmdExe")
public class AuthorityFindDirectCmdExe {

  private final AuthorityGateway authorityGateway;
  private final AuthorityConvertor authorityConvertor;

  @Autowired
  public AuthorityFindDirectCmdExe(AuthorityGateway authorityGateway,
    AuthorityConvertor authorityConvertor) {
    this.authorityGateway = authorityGateway;
    this.authorityConvertor = authorityConvertor;
  }

  public Page<AuthorityFindDirectCo> execute(AuthorityFindDirectCmd authorityFindDirectCmd) {
    return Optional.ofNullable(authorityFindDirectCmd).map(authorityFindDirectCmdNotNull -> {
      Page<Authority> authorities = authorityGateway.findDirectAuthorities(
        authorityFindDirectCmd.getAncestorId(),
        authorityFindDirectCmdNotNull.getCurrent(), authorityFindDirectCmdNotNull.getPageSize());
      List<AuthorityFindDirectCo> authorityFindDirectCos = authorities.getContent().stream()
        .map(authorityConvertor::toAuthorityFindDirectCo)
        .filter(Optional::isPresent).map(Optional::get).toList();
      return new PageImpl<>(authorityFindDirectCos, authorities.getPageable(),
        authorities.getTotalElements());
    }).orElse(new PageImpl<>(new ArrayList<>()));
  }
}
