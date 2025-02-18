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
package baby.mumu.basis;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import baby.mumu.basis.filters.TraceIdFilter;
import baby.mumu.basis.kotlin.tools.SpringContextUtils;
import baby.mumu.basis.spring.ApplicationProperties;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 基础模块配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class BasisConfiguration {

  @Bean
  @Order(HIGHEST_PRECEDENCE)
  public SpringContextUtils springContextUtil() {
    return new SpringContextUtils();
  }

  @Bean
  @ConditionalOnClass(Tracer.class)
  public FilterRegistrationBean<TraceIdFilter> mumuTraceIdFilter(
    ObjectProvider<Tracer> tracer) {
    FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new TraceIdFilter(tracer.getIfAvailable()));
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
    return registrationBean;
  }
}
