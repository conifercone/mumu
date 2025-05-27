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

import baby.mumu.extension.aspects.AspectConfiguration;
import baby.mumu.extension.cors.MuMuCorsConfiguration;
import baby.mumu.extension.distributed.lock.zookeeper.ZookeeperConfiguration;
import baby.mumu.extension.fd.FaceDetectionConfiguration;
import baby.mumu.extension.filters.FilterConfiguration;
import baby.mumu.extension.gson.GsonConfiguration;
import baby.mumu.extension.idempotent.IdempotentConfiguration;
import baby.mumu.extension.listener.ListenerConfiguration;
import baby.mumu.extension.nosql.DocumentConfiguration;
import baby.mumu.extension.ocr.OcrConfiguration;
import baby.mumu.extension.processor.grpc.GrpcExceptionAdvice;
import baby.mumu.extension.processor.response.ResponseBodyProcessor;
import baby.mumu.extension.sql.DatasourceConfiguration;
import baby.mumu.extension.translation.TranslationConfiguration;
import io.micrometer.observation.ObservationPredicate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * 拓展模块配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@Import({GrpcExceptionAdvice.class, ResponseBodyProcessor.class,
  ZookeeperConfiguration.class, MuMuCorsConfiguration.class,
  DatasourceConfiguration.class,
  TranslationConfiguration.class, AspectConfiguration.class, OcrConfiguration.class,
  FaceDetectionConfiguration.class, DocumentConfiguration.class, ListenerConfiguration.class,
  FilterConfiguration.class, IdempotentConfiguration.class, GsonConfiguration.class})
@EnableConfigurationProperties(ExtensionProperties.class)
public class ExtensionConfiguration {

  @Bean
  @ConditionalOnClass(ObservationPredicate.class)
  ObservationPredicate noActuatorServerObservations() {
    return (name, context) -> {
      if ("http.server.requests".equals(name)
        && context instanceof ServerRequestObservationContext serverContext) {
        return !serverContext.getCarrier().getRequestURI().startsWith("/actuator");
      } else {
        return true;
      }
    };
  }
}
