
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

package baby.mumu.log.application.system.convertor;

import baby.mumu.basis.kotlin.tools.TraceIdUtils;
import baby.mumu.genix.client.api.PrimaryKeyGrpcService;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.SystemLogFindAllCmd;
import baby.mumu.log.client.cmds.SystemLogSaveCmd;
import baby.mumu.log.client.cmds.SystemLogSubmitCmd;
import baby.mumu.log.client.dto.SystemLogFindAllDTO;
import baby.mumu.log.domain.system.SystemLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class SystemLogAssemblerConvertor {
    private final PrimaryKeyGrpcService primaryKeyGrpcService;

    public SystemLogAssemblerConvertor(PrimaryKeyGrpcService primaryKeyGrpcService) {
        this.primaryKeyGrpcService = primaryKeyGrpcService;
    }

    public Optional<SystemLog> toEntity(SystemLogSubmitCmd cmd) {
        return Optional.ofNullable(cmd).map(res -> {
            SystemLog log = SystemLogAssemblerMapper.INSTANCE.toEntity(res);
            String traceId = TraceIdUtils.getTraceId();
            log.setId(StringUtils.isNotBlank(traceId) ? traceId : String.valueOf(primaryKeyGrpcService.snowflake()));
            log.setRecordTime(LocalDateTime.now(ZoneId.of("UTC")));
            return log;
        });
    }

    public Optional<SystemLog> toEntity(SystemLogSaveCmd cmd) {
        return Optional.ofNullable(cmd).map(SystemLogAssemblerMapper.INSTANCE::toEntity);
    }

    public Optional<SystemLog> toEntity(SystemLogFindAllCmd cmd) {
        return Optional.ofNullable(cmd).map(SystemLogAssemblerMapper.INSTANCE::toEntity).map(log -> {
            Optional.ofNullable(cmd.getRecordStartTime()).ifPresent(log::setRecordStartTime);
            Optional.ofNullable(cmd.getRecordEndTime()).ifPresent(log::setRecordEndTime);
            return log;
        });
    }

    public Optional<SystemLogFindAllDTO> toSystemLogFindAllDTO(SystemLog log) {
        return Optional.ofNullable(log).map(SystemLogAssemblerMapper.INSTANCE::toSystemLogFindAllDTO);
    }

    public Optional<SystemLogSubmitCmd> toSystemLogSubmitCmd(SystemLogSubmitGrpcCmd cmd) {
        return Optional.ofNullable(cmd).map(SystemLogAssemblerMapper.INSTANCE::toSystemLogSubmitCmd);
    }
}
