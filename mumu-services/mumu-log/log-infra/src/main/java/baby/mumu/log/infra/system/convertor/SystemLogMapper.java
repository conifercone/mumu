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

package baby.mumu.log.infra.system.convertor;

import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.SystemLogFindAllCmd;
import baby.mumu.log.client.cmds.SystemLogSaveCmd;
import baby.mumu.log.client.cmds.SystemLogSubmitCmd;
import baby.mumu.log.client.dto.SystemLogFindAllDTO;
import baby.mumu.log.domain.system.SystemLog;
import baby.mumu.log.infra.system.gatewayimpl.elasticsearch.po.SystemLogEsPO;
import baby.mumu.log.infra.system.gatewayimpl.kafka.po.SystemLogKafkaPO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * SystemLog mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemLogMapper {

  SystemLogMapper INSTANCE = Mappers.getMapper(SystemLogMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLogKafkaPO toSystemLogKafkaPO(SystemLog systemLog);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLogEsPO toSystemLogEsPO(SystemLog systemLog);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLog toEntity(SystemLogSubmitCmd systemLogSubmitCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLog toEntity(SystemLogSaveCmd systemLogSaveCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLog toEntity(SystemLogEsPO systemLogEsPO);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLog toEntity(SystemLogFindAllCmd systemLogFindAllCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLogFindAllDTO toSystemLogFindAllDTO(SystemLog systemLog);

  @API(status = Status.STABLE, since = "1.0.1")
  SystemLogSaveCmd toSystemLogSaveCmd(SystemLogKafkaPO systemLogKafkaPO);

  @API(status = Status.STABLE, since = "2.2.0")
  SystemLogSubmitCmd toSystemLogSubmitCmd(SystemLogSubmitGrpcCmd systemLogSubmitGrpcCmd);
}
