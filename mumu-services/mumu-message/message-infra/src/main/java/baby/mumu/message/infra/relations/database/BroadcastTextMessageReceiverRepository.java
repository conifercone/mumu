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

package baby.mumu.message.infra.relations.database;

import baby.mumu.basis.enums.MessageStatusEnum;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 文本广播消息接收者关系管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
public interface BroadcastTextMessageReceiverRepository extends
  BaseJpaRepository<BroadcastTextMessageReceiverPO, BroadcastTextMessageReceiverPOId>,
  JpaSpecificationExecutor<BroadcastTextMessageReceiverPO> {

  List<BroadcastTextMessageReceiverPO> findByBroadcastTextMessageId(Long messageId);

  @Query("select distinct b.id.receiverId from BroadcastTextMessageReceiverPO b where b.messageStatus=:messageStatus and b.id.messageId=:messageId")
  List<Long> findReceiverIdsByMessageIdAndMessageStatus(
    @Param("messageId") Long messageId, @Param("messageStatus") MessageStatusEnum messageStatus);

  @Query("select distinct b.id.receiverId from BroadcastTextMessageReceiverPO b where b.id.messageId=:messageId")
  List<Long> findReceiverIdsByMessageId(
    @Param("messageId") Long messageId);

  @Query("select count(distinct b.id.receiverId) from BroadcastTextMessageReceiverPO b where b.messageStatus=:messageStatus and b.id.messageId=:messageId")
  Long countByMessageIdAndMessageStatus(@Param("messageId") Long messageId,
    @Param("messageStatus") MessageStatusEnum messageStatus);

  void deleteByBroadcastTextMessageId(Long messageId);
}
