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

package baby.mumu.storage.client.cmds;

import baby.mumu.basis.enums.StorageZonePolicyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

/**
 * 存储区域新增数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@Data
public class StorageZoneAddCmd {

    @Schema(description = "存储区域编码", requiredMode = RequiredMode.NOT_REQUIRED)
    private String code;

    @Schema(description = "存储区域名称", requiredMode = RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "存储区域描述", requiredMode = RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "存储区域策略", requiredMode = RequiredMode.NOT_REQUIRED)
    private StorageZonePolicyEnum policy;
}
