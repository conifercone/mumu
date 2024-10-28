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

import baby.mumu.basis.kotlin.tools.CommonUtil;
import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import baby.mumu.message.client.dto.co.BroadcastTextMessageForwardCo;
import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageArchivedDo;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageDo;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
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
public interface BroadcastTextMessageMapper {

  BroadcastTextMessageMapper INSTANCE = Mappers.getMapper(BroadcastTextMessageMapper.class);

  @API(status = Status.STABLE, since = "1.0.2")
  BroadcastTextMessage toEntity(
    BroadcastTextMessageForwardCo broadcastTextMessageForwardCo);

  @API(status = Status.STABLE, since = "1.0.2")
  BroadcastTextMessageDo toDataObject(BroadcastTextMessage broadcastTextMessage);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessage toEntity(BroadcastTextMessageDo broadcastTextMessageDo);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessage toEntity(
    BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd);

  @API(status = Status.STABLE, since = "1.0.4")
  BroadcastTextMessageArchivedDo toArchiveDo(BroadcastTextMessageDo broadcastTextMessageDo);

  @API(status = Status.STABLE, since = "1.0.4")
  BroadcastTextMessageDo toDataObject(
    BroadcastTextMessageArchivedDo broadcastTextMessageArchivedDo);

  @API(status = Status.STABLE, since = "1.0.3")
  BroadcastTextMessageFindAllYouSendCo toFindAllYouSendCo(
    BroadcastTextMessage broadcastTextMessage);

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget BroadcastTextMessageFindAllYouSendCo broadcastTextMessageFindAllYouSendCo) {
    CommonUtil.convertToAccountZone(broadcastTextMessageFindAllYouSendCo);
  }
}
