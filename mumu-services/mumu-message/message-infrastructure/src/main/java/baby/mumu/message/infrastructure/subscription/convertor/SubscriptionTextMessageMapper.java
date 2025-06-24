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

package baby.mumu.message.infrastructure.subscription.convertor;

import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.cmds.SubscriptionTextMessageForwardCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllWithSomeOneDTO;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllYouSendDTO;
import baby.mumu.message.domain.subscription.SubscriptionTextMessage;
import baby.mumu.message.infrastructure.subscription.gatewayimpl.database.po.SubscriptionTextMessageArchivedPO;
import baby.mumu.message.infrastructure.subscription.gatewayimpl.database.po.SubscriptionTextMessagePO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * SubscriptionTextMessage mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionTextMessageMapper extends DataTransferObjectMapper {

  SubscriptionTextMessageMapper INSTANCE = Mappers.getMapper(SubscriptionTextMessageMapper.class);

  @API(status = Status.STABLE, since = "1.0.2")
  SubscriptionTextMessage toEntity(
    SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd);

  @API(status = Status.STABLE, since = "1.0.2")
  SubscriptionTextMessagePO toPO(SubscriptionTextMessage subscriptionTextMessage);

  @API(status = Status.STABLE, since = "1.0.3")
  SubscriptionTextMessage toEntity(SubscriptionTextMessagePO subscriptionTextMessagePO);

  @API(status = Status.STABLE, since = "1.0.3")
  SubscriptionTextMessage toEntity(
    SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd);

  @API(status = Status.STABLE, since = "1.0.3")
  SubscriptionTextMessageFindAllYouSendDTO toFindAllYouSendDTO(
    SubscriptionTextMessage subscriptionTextMessage);

  @API(status = Status.STABLE, since = "1.0.3")
  SubscriptionTextMessageFindAllWithSomeOneDTO toFindAllWithSomeOne(
    SubscriptionTextMessage subscriptionTextMessage);

  @API(status = Status.STABLE, since = "1.0.4")
  SubscriptionTextMessageArchivedPO toArchivePO(
    SubscriptionTextMessagePO subscriptionTextMessagePO);

  @API(status = Status.STABLE, since = "1.0.4")
  SubscriptionTextMessagePO toPO(
    SubscriptionTextMessageArchivedPO subscriptionTextMessageArchivedPO);
}
