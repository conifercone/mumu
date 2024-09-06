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
package baby.mumu.log.domain.system.gateway;

import baby.mumu.log.domain.system.SystemLog;
import org.springframework.data.domain.Page;

/**
 * 系统日志领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface SystemLogGateway {

  /**
   * 提交系统日志
   *
   * @param systemLog 系统日志领域对象
   */
  void submit(SystemLog systemLog);

  /**
   * 保存系统日志
   *
   * @param systemLog 系统日志领域对象
   */
  void save(SystemLog systemLog);

  /**
   * 分页查询系统日志
   *
   * @param systemLog 查询条件
   * @param pageNo    当前页
   * @param pageSize  每页数量
   * @return 查询结果
   */
  Page<SystemLog> findAll(SystemLog systemLog, int pageNo, int pageSize);
}
