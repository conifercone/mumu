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
package com.sky.centaur.log.infrastructure.system.convertor;

import com.sky.centaur.log.client.dto.co.SystemLogFindAllCo;
import com.sky.centaur.log.client.dto.co.SystemLogSaveCo;
import com.sky.centaur.log.client.dto.co.SystemLogSubmitCo;
import com.sky.centaur.log.domain.system.SystemLog;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.elasticsearch.dataobject.SystemLogEsDo;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.kafka.dataobject.SystemLogKafkaDo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * SystemLog mapstruct转换器
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SystemLogMapper {

  SystemLogMapper INSTANCE = Mappers.getMapper(SystemLogMapper.class);

  SystemLogKafkaDo toKafkaDataObject(SystemLog systemLog);

  SystemLogEsDo toEsDataObject(SystemLog systemLog);

  @Mappings(value = {
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "recordEndTime", ignore = true),
      @Mapping(target = "recordStartTime", ignore = true),
      @Mapping(target = "recordTime", ignore = true)
  })
  SystemLog toEntity(SystemLogSubmitCo systemLogSubmitCo);

  @Mappings(value = {
      @Mapping(target = "recordEndTime", ignore = true),
      @Mapping(target = "recordStartTime", ignore = true)
  })
  SystemLog toEntity(SystemLogSaveCo systemLogSaveCo);

  @Mappings(value = {
      @Mapping(target = "recordEndTime", ignore = true),
      @Mapping(target = "recordStartTime", ignore = true)
  })
  SystemLog toEntity(SystemLogEsDo systemLogEsDo);

  @Mappings(value = {
      @Mapping(target = "recordEndTime", ignore = true),
      @Mapping(target = "recordStartTime", ignore = true)
  })
  SystemLog toEntity(SystemLogFindAllCo systemLogFindAllCo);

  SystemLogFindAllCo toFindAllCo(SystemLog systemLog);

  SystemLogSaveCo toSaveCo(SystemLogKafkaDo systemLogKafkaDo);
}
