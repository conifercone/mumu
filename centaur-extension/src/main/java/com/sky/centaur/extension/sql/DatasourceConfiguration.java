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
package com.sky.centaur.extension.sql;

import com.google.common.base.Strings;
import com.p6spy.engine.spy.P6DataSource;
import com.sky.centaur.basis.constants.BeanNameConstant;
import com.sky.centaur.basis.dataobject.jpa.CentaurJpaAuditorAware;
import com.sky.centaur.extension.ExtensionProperties;
import com.sky.centaur.extension.sql.filter.datasource.DataSourceFilter;
import com.sky.centaur.extension.sql.filter.datasource.DatasourceFilterChain;
import com.sky.centaur.extension.sql.filter.datasource.DatasourceFilterChainImpl;
import com.sky.centaur.extension.sql.filter.datasource.P6spyDataSourceFilter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.util.Assert;

/**
 * 数据源配置类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
@ConditionalOnProperty(prefix = "centaur.extension.sql", value = "enabled", havingValue = "true")
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
      ExtensionProperties extensionProperties) {
    DataSourceProperties dataSourceProperties = dataSourcePropertiesObjectProvider.getIfAvailable();
    Assert.notNull(dataSourceProperties, "No data source properties found");
    HikariConfig config = new HikariConfig();
    config.setUsername(dataSourceProperties.getUsername());
    config.setPassword(dataSourceProperties.getPassword());
    config.setJdbcUrl(dataSourceProperties.getUrl());
    String driverClassName = dataSourceProperties.getDriverClassName();
    if (!Strings.isNullOrEmpty(driverClassName)) {
      config.setDriverClassName(driverClassName);
    }
    HikariDataSource hikariDataSource = new HikariDataSource(config);
    return dataSourceFilterChain.doAfterFilter(hikariDataSource, extensionProperties);
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

  @Bean
  public CentaurJpaAuditorAware centaurJpaAuditorAware() {
    return new CentaurJpaAuditorAware();
  }

  @Bean(BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  public DataSourceTransactionManager transactionManager(Environment environment,
      DataSource dataSource,
      ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
    DataSourceTransactionManager transactionManager = createTransactionManager(environment,
        dataSource);
    transactionManagerCustomizers
        .ifAvailable(
            (customizers) -> customizers.customize((TransactionManager) transactionManager));
    return transactionManager;
  }

  private @NotNull DataSourceTransactionManager createTransactionManager(
      @NotNull Environment environment, DataSource dataSource) {
    return environment.getProperty("spring.dao.exceptiontranslation.enabled", Boolean.class,
        Boolean.TRUE)
        ? new JdbcTransactionManager(dataSource) : new DataSourceTransactionManager(dataSource);
  }
}
