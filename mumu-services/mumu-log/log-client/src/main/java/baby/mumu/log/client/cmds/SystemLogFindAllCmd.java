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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 系统日志查询所有指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Data
public class SystemLogFindAllCmd {

    /**
     * 唯一标识
     */
    @Schema(description = "日志ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private String id;

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

    /**
     * 系统日志的记录时间
     */
    @Schema(description = "记录时间", requiredMode = RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;

    /**
     * 系统日志的开始记录时间
     */
    @Schema(description = "记录开始时间", requiredMode = RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordStartTime;

    /**
     * 系统日志的结束记录时间
     */
    @Schema(description = "记录结束时间", requiredMode = RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordEndTime;

    /**
     * 当前页码
     */
    @Schema(description = "当前页", requiredMode = RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "{current.validation.min.size}")
    private Integer current = 1;
    /**
     * 每页数量
     */
    @Schema(description = "每页数量", requiredMode = RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "{page.size.validation.min.size}")
    private Integer pageSize = 10;
}
