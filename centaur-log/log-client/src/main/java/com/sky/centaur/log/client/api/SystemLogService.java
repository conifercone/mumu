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
package com.sky.centaur.log.client.api;

import com.sky.centaur.log.client.dto.SystemLogFindAllCmd;
import com.sky.centaur.log.client.dto.SystemLogSaveCmd;
import com.sky.centaur.log.client.dto.SystemLogSubmitCmd;
import com.sky.centaur.log.client.dto.co.SystemLogFindAllCo;
import org.springframework.data.domain.Page;

/**
 * 系统日志api
 *
 * @author kaiyu.shan
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
  Page<SystemLogFindAllCo> findAll(SystemLogFindAllCmd systemLogFindAllCmd);
}
