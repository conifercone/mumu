/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.log.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sky.centaur.log.client.dto.co.OperationLogFindAllCo;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 操作日志查询所有指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
public class OperationLogFindAllCmd {

  private OperationLogFindAllCo operationLogFindAllCo;
  /**
   * 操作日志的开始操作时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime operatingStartTime;

  /**
   * 操作日志的结束操作时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime operatingEndTime;
  /**
   * 当前页码
   */
  @Min(value = 0, message = "{page.no.validation.min.size}")
  private int pageNo = 0;
  /**
   * 每页数量
   */
  @Min(value = 1, message = "{page.size.validation.min.size}")
  private int pageSize = 10;
}
