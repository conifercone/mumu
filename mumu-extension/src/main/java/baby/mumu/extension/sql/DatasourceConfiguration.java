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
package baby.mumu.extension.sql;

import baby.mumu.basis.constants.BeanNameConstants;
import baby.mumu.basis.po.jpa.MuMuJpaAuditorAware;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.sql.filter.datasource.DataSourceFilter;
import baby.mumu.extension.sql.filter.datasource.DatasourceFilterChain;
import baby.mumu.extension.sql.filter.datasource.DatasourceFilterChainImpl;
import baby.mumu.extension.sql.filter.datasource.P6spyDataSourceFilter;
import com.p6spy.engine.spy.P6DataSource;
import java.util.List;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * 数据源配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
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
  @ConditionalOnClass(P6DataSource.class)
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public DataSourceFilter p6spyWrapperDataSourceFilter() {
    return new P6spyDataSourceFilter();
  }

  @Bean
  public static BeanPostProcessor dataSourceWrapperProcessor(
    DatasourceFilterChain dataSourceFilterChain,
    ExtensionProperties extensionProperties) {
    return new BeanPostProcessor() {
      @Override
      public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
        throws BeansException {
        if (bean instanceof DataSource originalDataSource) {
          // 包装原始数据源
          return dataSourceFilterChain.doAfterFilter(originalDataSource, extensionProperties);
        }
        return bean;
      }
    };
  }

  /**
   * 创建包含一组DataSourceFilter的DatasourceFilterChain的新实例并返回
   *
   * @param dataSourceFilters 用于构建DatasourceFilterChain的DataSourceFilter列表
   * @return 代表包含指定DataSourceFilter的FilterChain的实例
   */
  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public DatasourceFilterChain dataSourceFilterChain(List<DataSourceFilter> dataSourceFilters) {
    return new DatasourceFilterChainImpl(dataSourceFilters);
  }

  /**
   * 自定义JPA创建人修改人自动注入实例
   *
   * @return JpaAuditorAware
   */
  @Bean(name = BeanNameConstants.MUMU_JPA_AUDITOR_AWARE)
  public MuMuJpaAuditorAware mumuJpaAuditorAware() {
    return new MuMuJpaAuditorAware();
  }
}
