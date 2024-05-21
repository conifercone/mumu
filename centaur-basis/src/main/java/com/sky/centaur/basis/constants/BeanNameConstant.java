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
package com.sky.centaur.basis.constants;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * bean实例名称常量
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@API(status = Status.STABLE, since = "1.0.0")
public final class BeanNameConstant {

  /**
   * 默认事务管理器bean名称
   */
  public static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";

}

