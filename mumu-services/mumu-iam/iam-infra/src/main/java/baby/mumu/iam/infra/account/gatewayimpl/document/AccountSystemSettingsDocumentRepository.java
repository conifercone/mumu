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

package baby.mumu.iam.infra.account.gatewayimpl.document;

import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountSystemSettingsDocumentPO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 账号系统设置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
public interface AccountSystemSettingsDocumentRepository extends
  MongoRepository<AccountSystemSettingsDocumentPO, String> {

  List<AccountSystemSettingsDocumentPO> findByAccountId(@NotNull Long accountId);

  boolean existsByAccountIdAndProfile(@NotNull Long accountId, @NotBlank String profile);

  void deleteByAccountId(Long accountId);
}
