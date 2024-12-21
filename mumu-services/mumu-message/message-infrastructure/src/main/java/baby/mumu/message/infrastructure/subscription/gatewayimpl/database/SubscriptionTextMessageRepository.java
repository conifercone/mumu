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
package baby.mumu.message.infrastructure.subscription.gatewayimpl.database;

import baby.mumu.message.infrastructure.subscription.gatewayimpl.database.dataobject.SubscriptionTextMessageDO;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 订阅文本消息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
public interface SubscriptionTextMessageRepository extends
  BaseJpaRepository<SubscriptionTextMessageDO, Long>,
  JpaSpecificationExecutor<SubscriptionTextMessageDO> {

  Optional<SubscriptionTextMessageDO> findByIdAndReceiverId(@NotNull Long id,
    @NotNull Long receiverId);

  Optional<SubscriptionTextMessageDO> findByIdAndSenderId(@NotNull Long id,
    @NotNull Long senderId);

  void deleteByIdAndSenderId(@NotNull Long id, @NotNull Long senderId);
}
