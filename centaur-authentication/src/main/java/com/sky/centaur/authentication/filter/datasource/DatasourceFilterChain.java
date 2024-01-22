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
package com.sky.centaur.authentication.filter.datasource;

import com.sky.centaur.authentication.infrastructure.config.AuthenticationProperties;
import javax.sql.DataSource;

/**
 * 数据源过滤器链条接口
 *
 * @author 单开宇
 * @since 2024-01-22
 */
public interface DatasourceFilterChain {

  /**
   * 数据源创建后处理，主要处理数据源
   *
   * @param dataSource               处理前数据源
   * @param authenticationProperties 服务配置信息
   * @return 处理后数据源
   */
  DataSource doAfterFilter(DataSource dataSource,
      AuthenticationProperties authenticationProperties);
}
