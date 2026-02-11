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

package baby.mumu.log.client.cmds;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

/**
 * 系统日志提交指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Data
public class SystemLogSubmitCmd {

    /**
     * 日志内容
     */
    @Schema(description = "日志内容", requiredMode = RequiredMode.NOT_REQUIRED)
    private String content;

    /**
     * 系统日志的种类
     */
    @Schema(description = "系统日志种类", requiredMode = RequiredMode.NOT_REQUIRED)
    private String category;

    /**
     * 系统日志成功的文本模板
     */
    @Schema(description = "成功文本模板", requiredMode = RequiredMode.NOT_REQUIRED)
    private String success;

    /**
     * 系统日志失败的文本模板
     */
    @Schema(description = "失败文本模板", requiredMode = RequiredMode.NOT_REQUIRED)
    private String fail;
}
