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

package baby.mumu.genix.application.service;

import baby.mumu.genix.application.pk.executor.PrimaryKeySnowflakeGenerateExe;
import baby.mumu.genix.client.api.PrimaryKeyService;
import baby.mumu.genix.client.dto.PrimaryKeySnowflakeDTO;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 主键生成
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Service
@Observed(name = "PrimaryKeyServiceImpl")
public class PrimaryKeyServiceImpl implements PrimaryKeyService {

    private final PrimaryKeySnowflakeGenerateExe primaryKeySnowflakeGenerateExe;

    @Autowired
    public PrimaryKeyServiceImpl(PrimaryKeySnowflakeGenerateExe primaryKeySnowflakeGenerateExe) {
        this.primaryKeySnowflakeGenerateExe = primaryKeySnowflakeGenerateExe;
    }

    @Override
    public PrimaryKeySnowflakeDTO snowflake() {
        PrimaryKeySnowflakeDTO primaryKeySnowflakeDTO = new PrimaryKeySnowflakeDTO();
        primaryKeySnowflakeDTO.setId(primaryKeySnowflakeGenerateExe.execute());
        return primaryKeySnowflakeDTO;
    }
}
