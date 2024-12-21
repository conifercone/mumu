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
package baby.mumu.message.infrastructure.broadcast.convertor;

import baby.mumu.basis.mappers.ClientObjectMapper;
import baby.mumu.message.client.cmds.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.cmds.BroadcastTextMessageForwardCmd;
import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendDTO;
import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageArchivedDO;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageDO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * BroadcastTextMessage mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BroadcastTextMessageMapper extends ClientObjectMapper {

  BroadcastTextMessageMapper INSTANCE = Mappers.getMapper(BroadcastTextMessageMapper.class);

  @API(status = Status.STABLE, since = "1.0.2")
  BroadcastTextMessage toEntity(
    BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd);

  @API(status = Status.STABLE, since = "1.0.2")
  BroadcastTextMessageDO toDataObject(BroadcastTextMessage broadcastTextMessage);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessage toEntity(BroadcastTextMessageDO broadcastTextMessageDo);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessage toEntity(
    BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd);

  @API(status = Status.STABLE, since = "1.0.4")
  BroadcastTextMessageArchivedDO toArchiveDO(BroadcastTextMessageDO broadcastTextMessageDo);

  @API(status = Status.STABLE, since = "1.0.4")
  BroadcastTextMessageDO toDataObject(
    BroadcastTextMessageArchivedDO broadcastTextMessageArchivedDo);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessageFindAllYouSendDTO toFindAllYouSendDTO(
    BroadcastTextMessage broadcastTextMessage);
}
