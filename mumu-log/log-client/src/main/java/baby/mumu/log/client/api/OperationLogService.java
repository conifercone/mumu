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

import baby.mumu.log.client.dto.OperationLogFindAllCmd;
import baby.mumu.log.client.dto.OperationLogSaveCmd;
import baby.mumu.log.client.dto.OperationLogSubmitCmd;
import baby.mumu.log.client.dto.co.OperationLogFindAllCo;
import baby.mumu.log.client.dto.co.OperationLogQryCo;
import org.springframework.data.domain.Page;

/**
 * 操作日志api
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface OperationLogService {

  /**
   * 操作日志提交
   *
   * @param operationLogSubmitCmd 操作日志提交指令
   */
  void submit(OperationLogSubmitCmd operationLogSubmitCmd);

  /**
   * 操作日志保存
   *
   * @param operationLogSaveCmd 操作日志保存指令
   */
  void save(OperationLogSaveCmd operationLogSaveCmd);

  /**
   * 根据id查询操作日志
   *
   * @param id 操作日志id
   * @return 查询结果
   */
  OperationLogQryCo findOperationLogById(String id);

  /**
   * 分页查询操作日志
   *
   * @param operationLogFindAllCmd 分页查询操作日志指令
   * @return 查询结果
   */
  Page<OperationLogFindAllCo> findAll(OperationLogFindAllCmd operationLogFindAllCmd);
}
