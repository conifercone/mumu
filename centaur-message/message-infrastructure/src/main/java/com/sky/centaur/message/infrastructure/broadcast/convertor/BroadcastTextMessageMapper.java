/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.message.infrastructure.broadcast.convertor;

import com.sky.centaur.message.client.dto.co.BroadcastTextMessageForwardCo;
import com.sky.centaur.message.domain.broadcast.BroadcastTextMessage;
import com.sky.centaur.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageDo;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * BroadcastTextMessage mapstruct转换器
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BroadcastTextMessageMapper {

  BroadcastTextMessageMapper INSTANCE = Mappers.getMapper(BroadcastTextMessageMapper.class);

  @API(status = Status.STABLE, since = "1.0.2")
  @Mappings(value = {
      @Mapping(target = "readQuantity", ignore = true),
      @Mapping(target = "senderId", ignore = true),
      @Mapping(target = "unreadQuantity", ignore = true)
  })
  BroadcastTextMessage toEntity(
      BroadcastTextMessageForwardCo broadcastTextMessageForwardCo);

  @API(status = Status.STABLE, since = "1.0.2")
  BroadcastTextMessageDo toDataObject(BroadcastTextMessage broadcastTextMessage);
}
