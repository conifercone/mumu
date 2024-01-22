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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sql.DataSource;

/**
 * 数据源过滤器链条接口实现
 *
 * @author 单开宇
 * @since 2024-01-22
 */
public class DatasourceFilterChainImpl implements DatasourceFilterChain {

  /**
   * 数据源过滤器列表
   */
  private final List<DataSourceFilter> filters = new CopyOnWriteArrayList<>();

  public DatasourceFilterChainImpl(List<DataSourceFilter> dataSourceFilters) {
    this.filters.addAll(dataSourceFilters);
  }


  @Override
  public DataSource doAfterFilter(DataSource dataSource,
      AuthenticationProperties authenticationProperties) {
    if (!filters.isEmpty()) {
      for (DataSourceFilter filter : filters) {
        dataSource = filter.afterCreate(dataSource, authenticationProperties);
      }
    }

    return dataSource;
  }
}
