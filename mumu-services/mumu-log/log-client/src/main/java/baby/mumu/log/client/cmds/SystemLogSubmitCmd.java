/*
 * Copyright (c) 2024-2025, the original author or authors.
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

import lombok.Data;

/**
 * 系统日志提交指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
public class SystemLogSubmitCmd {

  /**
   * 日志内容
   */
  private String content;

  /**
   * 系统日志的种类
   */
  private String category;

  /**
   * 系统日志成功的文本模板
   */
  private String success;

  /**
   * 系统日志失败的文本模板
   */
  private String fail;
}
