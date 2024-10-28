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
package baby.mumu.extension.sql;

import baby.mumu.basis.constants.BeanNameConstants;
import baby.mumu.basis.dataobject.jpa.MuMuJpaAuditorAware;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.sql.filter.datasource.DataSourceFilter;
import baby.mumu.extension.sql.filter.datasource.DatasourceFilterChain;
import baby.mumu.extension.sql.filter.datasource.DatasourceFilterChainImpl;
import baby.mumu.extension.sql.filter.datasource.P6spyDataSourceFilter;
import com.google.common.base.Strings;
import com.p6spy.engine.spy.P6DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

/**
 * 数据源配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
@ConditionalOnProperty(prefix = "mumu.extension.sql", value = "enabled", havingValue = "true")
public class DatasourceConfiguration {

  /**
   * 创建P6spyWrapperDataSourceFilter的新实例并返回
   *
   * @return 代表P6spyWrapperDataSourceFilter的DataSourceFilter
   */
  @Bean
  @Order(1000)
  @ConditionalOnClass(P6DataSource.class)
  public DataSourceFilter p6spyWrapperDataSourceFilter() {
    return new P6spyDataSourceFilter();
  }

  @Bean
  public DataSource datasource(
    ObjectProvider<DataSourceProperties> dataSourcePropertiesObjectProvider,
    DatasourceFilterChain dataSourceFilterChain,
    ExtensionProperties extensionProperties, HikariConfig hikariConfig) {
    DataSourceProperties dataSourceProperties = dataSourcePropertiesObjectProvider.getIfAvailable();
    Assert.notNull(dataSourceProperties, "No data source properties found");
    hikariConfig.setUsername(dataSourceProperties.getUsername());
    hikariConfig.setPassword(dataSourceProperties.getPassword());
    hikariConfig.setJdbcUrl(dataSourceProperties.getUrl());
    String driverClassName = dataSourceProperties.getDriverClassName();
    if (!Strings.isNullOrEmpty(driverClassName)) {
      hikariConfig.setDriverClassName(driverClassName);
    }
    HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
    return dataSourceFilterChain.doAfterFilter(hikariDataSource, extensionProperties);
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public HikariConfig hikariConfig() {
    return new HikariConfig();
  }

  /**
   * 创建包含一组DataSourceFilter的DatasourceFilterChain的新实例并返回
   *
   * @param dataSourceFilters 用于构建DatasourceFilterChain的DataSourceFilter列表
   * @return 代表包含指定DataSourceFilter的FilterChain的实例
   */
  @Bean
  public DatasourceFilterChain dataSourceFilterChain(List<DataSourceFilter> dataSourceFilters) {
    return new DatasourceFilterChainImpl(dataSourceFilters);
  }

  @Bean(name = BeanNameConstants.MUMU_JPA_AUDITOR_AWARE)
  public MuMuJpaAuditorAware mumuJpaAuditorAware() {
    return new MuMuJpaAuditorAware();
  }
}
