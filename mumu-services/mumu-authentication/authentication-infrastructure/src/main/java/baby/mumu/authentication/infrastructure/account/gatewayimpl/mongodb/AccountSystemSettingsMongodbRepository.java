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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb;

import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.dataobject.AccountSystemSettingsMongodbDo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 账户系统设置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
public interface AccountSystemSettingsMongodbRepository extends
    MongoRepository<AccountSystemSettingsMongodbDo, String> {

  List<AccountSystemSettingsMongodbDo> findByUserId(@NotNull Long userId);

  boolean existsByUserIdAndProfile(@NotNull Long userId, @NotBlank String profile);
}
