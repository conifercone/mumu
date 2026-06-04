
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

package baby.mumu.log.infra.system.convertor;

import baby.mumu.log.domain.system.SystemLog;
import baby.mumu.log.infra.system.gatewayimpl.elasticsearch.po.SystemLogEsPO;
import baby.mumu.log.infra.system.mapper.SystemLogEsPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 系统日志ES持久化转换器 (Infrastructure Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class SystemLogEsPersistenceConvertor {
    public Optional<SystemLogEsPO> toEsPO(SystemLog systemLog) {
        return Optional.ofNullable(systemLog).map(SystemLogEsPersistenceMapper.INSTANCE::toEsPO);
    }

    public Optional<SystemLog> toEntity(SystemLogEsPO esPO) {
        return Optional.ofNullable(esPO).map(SystemLogEsPersistenceMapper.INSTANCE::toEntity);
    }
}
