/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.log.domain.operation;

import baby.mumu.basis.annotations.GenerateDescription;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 操作日志领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@GenerateDescription
public class OperationLog {

  /**
   * 唯一标识
   */
  private String id;

  /**
   * 日志内容
   */
  private String content;

  /**
   * 操作日志的执行人
   */
  private String operator;

  /**
   * 操作日志绑定的业务对象标识
   */
  private String bizNo;

  /**
   * 操作日志的种类
   */
  private String category;

  /**
   * 扩展参数，记录操作日志的修改详情
   */
  private String detail;

  /**
   * 操作日志成功的文本模板
   */
  private String success;

  /**
   * 操作日志失败的文本模板
   */
  private String fail;

  /**
   * 操作日志的操作时间
   */
  private LocalDateTime operatingTime;

  /**
   * 操作日志的开始操作时间
   */
  private LocalDateTime operatingStartTime;


  /**
   * 操作日志的结束操作时间
   */
  private LocalDateTime operatingEndTime;
}
