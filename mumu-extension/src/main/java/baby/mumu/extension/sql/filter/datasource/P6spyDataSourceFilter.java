/*
 * Copyright (c) 2024-2026, the original author or authors.
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
import com.p6spy.engine.spy.P6DataSource;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * p6spy数据源过滤器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class P6spyDataSourceFilter extends AbstractDataSourceFilter {

    private static final Logger log = LoggerFactory.getLogger(P6spyDataSourceFilter.class);


    @Override
    public DataSource afterCreate(DataSource dataSource,
                                  @NonNull ExtensionProperties extensionProperties) {
        P6spyDataSourceFilter.log.debug("P6spyDataSourceFilter afterCreate starting...");
        boolean enableLog =
            extensionProperties.getSql().getLog().isEnabled();
        if (enableLog) {
            dataSource = new P6DataSource(dataSource);
            P6spyDataSourceFilter.log.debug("p6spy datasource wrapped datasource");
        }

        return dataSource;
    }
}
