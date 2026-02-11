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

package baby.mumu.iam.client.cmds;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 添加权限指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Data
public class PermissionAddCmd {

    @Schema(description = "权限编码", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "{permission.code.validation.size}")
    private String code;

    @Schema(description = "权限名称", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 200, message = "{permission.name.validation.size}")
    private String name;

    @Schema(description = "权限描述", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 500)
    private String description;
}
