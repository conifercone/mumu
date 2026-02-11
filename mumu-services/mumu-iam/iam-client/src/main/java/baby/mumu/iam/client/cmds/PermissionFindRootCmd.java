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
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取所有根权限指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Data
@EqualsAndHashCode
public class PermissionFindRootCmd {

    @Schema(description = "当前页", requiredMode = RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "{current.validation.min.size}")
    private Integer current = 1;

    @Schema(description = "每页数量", requiredMode = RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "{page.size.validation.min.size}")
    private Integer pageSize = 10;
}
