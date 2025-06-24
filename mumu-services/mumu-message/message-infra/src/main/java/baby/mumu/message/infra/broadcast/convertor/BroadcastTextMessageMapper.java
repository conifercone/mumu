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

package baby.mumu.message.infra.broadcast.convertor;

import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.message.client.cmds.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.cmds.BroadcastTextMessageForwardCmd;
import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendDTO;
import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import baby.mumu.message.infra.broadcast.gatewayimpl.database.po.BroadcastTextMessageArchivedPO;
import baby.mumu.message.infra.broadcast.gatewayimpl.database.po.BroadcastTextMessagePO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * BroadcastTextMessage mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BroadcastTextMessageMapper extends DataTransferObjectMapper {

  BroadcastTextMessageMapper INSTANCE = Mappers.getMapper(BroadcastTextMessageMapper.class);

  @API(status = Status.STABLE, since = "1.0.2")
  BroadcastTextMessage toEntity(
    BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd);

  @API(status = Status.STABLE, since = "1.0.2")
  BroadcastTextMessagePO toBroadcastTextMessagePO(BroadcastTextMessage broadcastTextMessage);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessage toEntity(BroadcastTextMessagePO broadcastTextMessagePO);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessage toEntity(
    BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd);

  @API(status = Status.STABLE, since = "1.0.4")
  BroadcastTextMessageArchivedPO toBroadcastTextMessageArchivedPO(
    BroadcastTextMessagePO broadcastTextMessagePO);

  @API(status = Status.STABLE, since = "1.0.4")
  BroadcastTextMessagePO toBroadcastTextMessagePO(
    BroadcastTextMessageArchivedPO broadcastTextMessageArchivedPO);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessageFindAllYouSendDTO toBroadcastTextMessageFindAllYouSendDTO(
    BroadcastTextMessage broadcastTextMessage);
}
