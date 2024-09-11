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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.database;

import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDo;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 账户基本信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
public interface AccountArchivedRepository extends BaseJpaRepository<AccountArchivedDo, Long>,
    JpaSpecificationExecutor<AccountArchivedDo> {

  /**
   * 根据id或者username或者email判断用户是否存在
   *
   * @param id       用户id
   * @param username 用户名
   * @param email    用户邮箱地址
   * @return 是否存在
   */
  boolean existsByIdOrUsernameOrEmail(Long id, @Size(max = 50) @NotNull String username,
      @Size(max = 200) String email);

  /**
   * 邮箱地址是否存在
   *
   * @param email 账户邮箱地址
   * @return true:邮箱地址已存在 false:邮箱地址不存在
   */
  boolean existsByEmail(String email);

  /**
   * 账户名是否存在
   *
   * @param username 账户名
   * @return true:账户名已存在 false:账户名不存在
   */
  boolean existsByUsername(String username);

}
