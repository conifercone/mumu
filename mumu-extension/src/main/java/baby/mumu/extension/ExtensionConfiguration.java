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

package baby.mumu.extension;

import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.extension.aspects.AspectConfiguration;
import baby.mumu.extension.fd.FaceDetectionConfiguration;
import baby.mumu.extension.filters.FilterConfiguration;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.grpc.interceptors.SafeBearerTokenInterceptor;
import baby.mumu.extension.idempotent.IdempotentConfiguration;
import baby.mumu.extension.listener.ListenerConfiguration;
import baby.mumu.extension.mvc.ApplicationMvcConfiguration;
import baby.mumu.extension.mvc.interceptor.TraceIdInterceptor;
import baby.mumu.extension.nosql.DocumentConfiguration;
import baby.mumu.extension.ocr.OcrConfiguration;
import baby.mumu.extension.processor.grpc.GrpcExceptionHandlersConfiguration;
import baby.mumu.extension.processor.response.ResponseBodyProcessor;
import baby.mumu.extension.sql.DatasourceConfiguration;
import baby.mumu.extension.translation.TranslationConfiguration;
import io.micrometer.observation.ObservationPredicate;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.GlobalClientInterceptor;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * 拓展模块配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Configuration
@Import({GrpcExceptionHandlersConfiguration.class, ResponseBodyProcessor.class,
  ApplicationMvcConfiguration.class,
  DatasourceConfiguration.class,
  TranslationConfiguration.class, AspectConfiguration.class, OcrConfiguration.class,
  FaceDetectionConfiguration.class, DocumentConfiguration.class, ListenerConfiguration.class,
  FilterConfiguration.class, IdempotentConfiguration.class})
@EnableConfigurationProperties(ExtensionProperties.class)
public class ExtensionConfiguration {

  @Bean
  public TraceIdInterceptor traceIdInterceptor(ObjectProvider<Tracer> tracerProvider) {
    return new TraceIdInterceptor(tracerProvider);
  }

  @Bean
  @ConditionalOnClass(ObservationPredicate.class)
  ObservationPredicate noActuatorServerObservations() {
    return (name, context) -> {
      if ("http.server.requests".equals(name)
        && context instanceof ServerRequestObservationContext serverContext) {
        assert serverContext.getCarrier() != null;
        return !serverContext.getCarrier().getRequestURI().startsWith("/actuator");
      } else {
        return true;
      }
    };
  }

  @Bean
  @GlobalServerInterceptor
  @Order(Integer.MIN_VALUE)
  ClientIpInterceptor clientIpInterceptor() {
    return new ClientIpInterceptor();
  }

  @Bean
  @GlobalClientInterceptor
  SafeBearerTokenInterceptor bearerTokenInterceptor() {
    return new SafeBearerTokenInterceptor(
      () -> SecurityContextUtils.getTokenValue().orElse(null)
    );
  }
}
