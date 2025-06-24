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

package baby.mumu.storage.configuration;

import baby.mumu.basis.spring.ApplicationProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 监控配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Configuration
public class MicrometerConfiguration {

  @Bean
  public ObservationGrpcClientInterceptor observationGrpcClientInterceptor(
    ObservationRegistry observationRegistry) {
    return new ObservationGrpcClientInterceptor(observationRegistry);
  }

  @Bean
  MeterRegistryCustomizer<MeterRegistry> configurer(ApplicationProperties applicationProperties) {
    return registry -> registry.config().commonTags("application", applicationProperties.getName());
  }
}
