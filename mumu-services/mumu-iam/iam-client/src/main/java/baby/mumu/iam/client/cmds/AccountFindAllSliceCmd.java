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

import java.util.List;

/**
 * 分页查询所有账号指令（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
public class AccountFindAllSliceCmd {

    /**
     * 账号id
     */
    @Schema(description = "账号ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 账号名
     */
    @Schema(description = "账号名", requiredMode = RequiredMode.NOT_REQUIRED)
    private String username;

    /**
     * 账号角色ID集合
     */
    @Schema(description = "账号角色ID集合", requiredMode = RequiredMode.NOT_REQUIRED)
    private List<Long> roleIds;

    /**
     * 国际电话区号
     */
    @Schema(description = "国际电话区号", requiredMode = RequiredMode.NOT_REQUIRED)
    private String phoneCountryCode;

    /**
     * 手机号
     */
    @Schema(description = "手机号", requiredMode = RequiredMode.NOT_REQUIRED)
    private String phone;

    /**
     * 电子邮件
     */
    @Schema(description = "电子邮件", requiredMode = RequiredMode.NOT_REQUIRED)
    private String email;

    @Schema(description = "当前页", requiredMode = RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "{current.validation.min.size}")
    private Integer current = 1;

    @Schema(description = "每页数量", requiredMode = RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "{page.size.validation.min.size}")
    private Integer pageSize = 10;
}
