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
package baby.mumu.log.client.api;

import baby.mumu.log.client.cmds.SystemLogFindAllCmd;
import baby.mumu.log.client.cmds.SystemLogSaveCmd;
import baby.mumu.log.client.cmds.SystemLogSubmitCmd;
import baby.mumu.log.client.dto.SystemLogFindAllDTO;
import org.springframework.data.domain.Page;

/**
 * 系统日志api
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface SystemLogService {

  /**
   * 系统日志提交
   *
   * @param systemLogSubmitCmd 系统日志提交指令
   */
  void submit(SystemLogSubmitCmd systemLogSubmitCmd);

  /**
   * 系统日志保存
   *
   * @param systemLogSaveCmd 系统日志保存指令
   */
  void save(SystemLogSaveCmd systemLogSaveCmd);

  /**
   * 分页查询系统日志
   *
   * @param systemLogFindAllCmd 分页查询系统日志指令
   * @return 查询结果
   */
  Page<SystemLogFindAllDTO> findAll(SystemLogFindAllCmd systemLogFindAllCmd);
}
