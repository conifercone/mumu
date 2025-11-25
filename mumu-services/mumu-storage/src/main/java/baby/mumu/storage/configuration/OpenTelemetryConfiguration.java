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

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmClassLoadingMeterConventions;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmCpuMeterConventions;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmMemoryMeterConventions;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmThreadMeterConventions;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.OpenTelemetryServerRequestObservationConvention;

/**
 * OpenTelemetry 配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.16.0
 */
@Configuration(proxyBeanMethods = false)
public class OpenTelemetryConfiguration {

  @Bean
  OpenTelemetryServerRequestObservationConvention openTelemetryServerRequestObservationConvention() {
    return new OpenTelemetryServerRequestObservationConvention();
  }

  @Bean
  OpenTelemetryJvmCpuMeterConventions openTelemetryJvmCpuMeterConventions() {
    return new OpenTelemetryJvmCpuMeterConventions(Tags.empty());
  }

  @Bean
  ProcessorMetrics processorMetrics() {
    return new ProcessorMetrics(List.of(), new OpenTelemetryJvmCpuMeterConventions(Tags.empty()));
  }

  @Bean
  JvmMemoryMetrics jvmMemoryMetrics() {
    return new JvmMemoryMetrics(List.of(),
      new OpenTelemetryJvmMemoryMeterConventions(Tags.empty()));
  }

  @Bean
  JvmThreadMetrics jvmThreadMetrics() {
    return new JvmThreadMetrics(List.of(),
      new OpenTelemetryJvmThreadMeterConventions(Tags.empty()));
  }

  @Bean
  ClassLoaderMetrics classLoaderMetrics() {
    return new ClassLoaderMetrics(new OpenTelemetryJvmClassLoadingMeterConventions());
  }

}
