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
package baby.mumu.log.infrastructure.operation.convertor;

import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.OperationLogFindAllCmd;
import baby.mumu.log.client.cmds.OperationLogSaveCmd;
import baby.mumu.log.client.cmds.OperationLogSubmitCmd;
import baby.mumu.log.client.dto.OperationLogFindAllDTO;
import baby.mumu.log.client.dto.OperationLogQryDTO;
import baby.mumu.log.domain.operation.OperationLog;
import baby.mumu.log.infrastructure.operation.gatewayimpl.elasticsearch.dataobject.OperationLogEsDO;
import baby.mumu.log.infrastructure.operation.gatewayimpl.kafka.dataobject.OperationLogKafkaDO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * OperationLog mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperationLogMapper extends GrpcMapper {

  OperationLogMapper INSTANCE = Mappers.getMapper(OperationLogMapper.class);

  OperationLogKafkaDO toKafkaDataObject(OperationLog operationLog);

  OperationLogEsDO toEsDataObject(OperationLog operationLog);

  @API(status = Status.STABLE, since = "1.0.1")
  OperationLog toEntity(OperationLogSubmitCmd operationLogSubmitCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  OperationLog toEntity(OperationLogSaveCmd operationLogSaveCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  OperationLog toEntity(OperationLogEsDO operationLogEsDo);

  @API(status = Status.STABLE, since = "1.0.1")
  OperationLog toEntity(OperationLogFindAllCmd operationLogFindAllCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  OperationLogFindAllDTO toFindAllDTO(OperationLog operationLog);

  @API(status = Status.STABLE, since = "1.0.1")
  OperationLogQryDTO toQryDTO(OperationLog operationLog);

  @API(status = Status.STABLE, since = "1.0.1")
  OperationLogSaveCmd toOperationLogSaveCmd(OperationLogKafkaDO operationLogKafkaDo);

  @API(status = Status.STABLE, since = "2.2.0")
  OperationLogSubmitCmd toOperationLogSubmitCmd(
    OperationLogSubmitGrpcCmd operationLogSubmitGrpcCmd);
}
