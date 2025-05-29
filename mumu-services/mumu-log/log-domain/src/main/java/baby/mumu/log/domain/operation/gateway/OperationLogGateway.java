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
package baby.mumu.log.domain.operation.gateway;

import baby.mumu.log.domain.operation.OperationLog;
import java.util.Optional;
import org.springframework.data.domain.Page;

/**
 * 操作日志领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public interface OperationLogGateway {

  /**
   * 提交操作日志
   *
   * @param operationLog 操作日志领域对象
   */
  void submit(OperationLog operationLog);

  /**
   * 保存操作日志
   *
   * @param operationLog 操作日志领域对象
   */
  void save(OperationLog operationLog);

  /**
   * 根据日志ID获取操作日志
   *
   * @param id 操作日志ID
   * @return 操作日志
   */
  Optional<OperationLog> findOperationLogById(String id);

  /**
   * 分页查询操作日志
   *
   * @param operationLog 查询条件
   * @param current      当前页
   * @param pageSize     每页数量
   * @return 查询结果
   */
  Page<OperationLog> findAll(OperationLog operationLog, int current, int pageSize);
}
