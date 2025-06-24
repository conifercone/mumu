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

package baby.mumu.message.infra.subscription.gatewayimpl.database;

import baby.mumu.message.infra.subscription.gatewayimpl.database.po.SubscriptionTextMessageArchivedPO;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 订阅文本归档消息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.3
 */
public interface SubscriptionTextMessageArchivedRepository extends
  BaseJpaRepository<SubscriptionTextMessageArchivedPO, Long>,
  JpaSpecificationExecutor<SubscriptionTextMessageArchivedPO> {

  void deleteByIdAndSenderId(@NotNull Long id, @NotNull Long senderId);

  Optional<SubscriptionTextMessageArchivedPO> findByIdAndSenderId(@NotNull Long id,
    @NotNull Long senderId);
}
