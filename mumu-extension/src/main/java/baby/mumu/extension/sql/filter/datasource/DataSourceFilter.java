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
package baby.mumu.extension.sql.filter.datasource;

import baby.mumu.extension.ExtensionProperties;
import javax.sql.DataSource;

/**
 * 顶级数据源过滤器接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface DataSourceFilter {

  /**
   * 数据源创建后处理，主要处理数据源
   *
   * @param dataSource          处理前数据源
   * @param extensionProperties 服务配置信息
   * @return 处理后数据源
   */
  DataSource afterCreate(DataSource dataSource, ExtensionProperties extensionProperties);
}
