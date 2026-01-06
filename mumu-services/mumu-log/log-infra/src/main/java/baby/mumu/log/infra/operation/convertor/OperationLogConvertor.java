/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.log.infra.operation.convertor;

import baby.mumu.basis.kotlin.tools.TraceIdUtils;
import baby.mumu.genix.client.api.PrimaryKeyGrpcService;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.OperationLogFindAllCmd;
import baby.mumu.log.client.cmds.OperationLogSaveCmd;
import baby.mumu.log.client.cmds.OperationLogSubmitCmd;
import baby.mumu.log.client.dto.OperationLogFindAllDTO;
import baby.mumu.log.client.dto.OperationLogQryDTO;
import baby.mumu.log.domain.operation.OperationLog;
import baby.mumu.log.infra.operation.gatewayimpl.elasticsearch.po.OperationLogEsPO;
import baby.mumu.log.infra.operation.gatewayimpl.kafka.po.OperationLogKafkaPO;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * 操作日志转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class OperationLogConvertor {


    private final PrimaryKeyGrpcService primaryKeyGrpcService;

    @Autowired
    public OperationLogConvertor(PrimaryKeyGrpcService primaryKeyGrpcService) {
        this.primaryKeyGrpcService = primaryKeyGrpcService;
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<OperationLogKafkaPO> toOperationLogKafkaPO(OperationLog operationLog) {
        return Optional.ofNullable(operationLog)
            .map(OperationLogMapper.INSTANCE::toOperationLogKafkaPO);
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<OperationLogEsPO> toOperationLogEsPO(OperationLog operationLog) {
        return Optional.ofNullable(operationLog).map(OperationLogMapper.INSTANCE::toOperationLogEsPO);
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<OperationLog> toEntity(OperationLogSubmitCmd operationLogSubmitCmd) {
        return Optional.ofNullable(operationLogSubmitCmd).map(res -> {
            OperationLog operationLog = OperationLogMapper.INSTANCE.toEntity(res);
            String traceId = TraceIdUtils.getTraceId();
            operationLog.setId(StringUtils.isNotBlank(traceId) ? traceId
                : String.valueOf(primaryKeyGrpcService.snowflake())
            );
            operationLog.setOperatingTime(LocalDateTime.now(ZoneId.of("UTC")));
            return operationLog;
        });

    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<OperationLog> toEntity(OperationLogSaveCmd operationLogSaveCmd) {
        return Optional.ofNullable(operationLogSaveCmd).map(OperationLogMapper.INSTANCE::toEntity);
    }


    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<OperationLog> toEntity(OperationLogEsPO operationLogEsPO) {
        return Optional.ofNullable(operationLogEsPO).map(OperationLogMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<OperationLog> toEntity(
        OperationLogFindAllCmd operationLogFindAllCmd) {
        return Optional.ofNullable(operationLogFindAllCmd)
            .map(OperationLogMapper.INSTANCE::toEntity).map(operationLog -> {
                Optional.ofNullable(operationLogFindAllCmd.getOperatingStartTime())
                    .ifPresent(operationLog::setOperatingStartTime);
                Optional.ofNullable(operationLogFindAllCmd.getOperatingEndTime())
                    .ifPresent(operationLog::setOperatingEndTime);
                return operationLog;
            });
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<OperationLogFindAllDTO> toOperationLogFindAllDTO(OperationLog operationLog) {
        return Optional.ofNullable(operationLog)
            .map(OperationLogMapper.INSTANCE::toOperationLogFindAllDTO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<OperationLogSubmitCmd> toOperationLogSubmitCmd(
        OperationLogSubmitGrpcCmd operationLogSubmitGrpcCmd) {
        return Optional.ofNullable(operationLogSubmitGrpcCmd)
            .map(OperationLogMapper.INSTANCE::toOperationLogSubmitCmd);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<OperationLogSaveCmd> toOperationLogSaveCmd(
        OperationLogKafkaPO operationLogKafkaPO) {
        return Optional.ofNullable(operationLogKafkaPO)
            .map(OperationLogMapper.INSTANCE::toOperationLogSaveCmd);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<OperationLogQryDTO> toOperationLogQryDTO(
        OperationLog operationLog) {
        return Optional.ofNullable(operationLog)
            .map(OperationLogMapper.INSTANCE::toOperationLogQryDTO);
    }
}
