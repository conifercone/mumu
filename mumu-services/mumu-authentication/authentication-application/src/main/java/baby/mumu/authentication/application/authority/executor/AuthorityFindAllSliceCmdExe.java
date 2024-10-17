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

import baby.mumu.authentication.client.dto.AuthorityFindAllSliceCmd;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllSliceCo;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.authority.gateway.AuthorityGateway;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 查询权限指令执行器（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "AuthorityFindAllSliceCmdExe")
public class AuthorityFindAllSliceCmdExe {

  private final AuthorityGateway authorityGateway;
  private final AuthorityConvertor authorityConvertor;

  @Autowired
  public AuthorityFindAllSliceCmdExe(AuthorityGateway authorityGateway,
      AuthorityConvertor authorityConvertor) {
    this.authorityGateway = authorityGateway;
    this.authorityConvertor = authorityConvertor;
  }

  public Slice<AuthorityFindAllSliceCo> execute(AuthorityFindAllSliceCmd authorityFindAllSliceCmd) {
    Assert.notNull(authorityFindAllSliceCmd, "AuthorityFindAllSliceCmd cannot be null");
    Authority authority = authorityConvertor.toEntity(
            authorityFindAllSliceCmd.getAuthorityFindAllSliceQueryCo())
        .orElseGet(Authority::new);
    Slice<Authority> authorities = authorityGateway.findAllSlice(authority,
        authorityFindAllSliceCmd.getCurrent(), authorityFindAllSliceCmd.getPageSize());
    List<AuthorityFindAllSliceCo> authorityFindAllSliceCoList = authorities.getContent().stream()
        .map(authorityConvertor::toFindAllSliceCo)
        .filter(Optional::isPresent).map(Optional::get).toList();
    return new SliceImpl<>(authorityFindAllSliceCoList, authorities.getPageable(),
        authorities.hasNext());
  }
}
