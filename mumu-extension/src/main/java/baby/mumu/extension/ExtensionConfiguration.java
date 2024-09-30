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
package baby.mumu.extension;

import baby.mumu.extension.aspects.AspectConfiguration;
import baby.mumu.extension.cors.MuMuCorsConfiguration;
import baby.mumu.extension.distributed.lock.zookeeper.ZookeeperConfiguration;
import baby.mumu.extension.fd.FaceDetectionConfiguration;
import baby.mumu.extension.ocr.OcrConfiguration;
import baby.mumu.extension.processor.grpc.GrpcExceptionAdvice;
import baby.mumu.extension.processor.response.ResponseBodyProcessor;
import baby.mumu.extension.sql.DatasourceConfiguration;
import baby.mumu.extension.translation.TranslationConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 拓展模块配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@Import({GrpcExceptionAdvice.class, ResponseBodyProcessor.class,
    ZookeeperConfiguration.class, MuMuCorsConfiguration.class, DatasourceConfiguration.class,
    TranslationConfiguration.class, AspectConfiguration.class, OcrConfiguration.class,
    FaceDetectionConfiguration.class})
@EnableConfigurationProperties(ExtensionProperties.class)
public class ExtensionConfiguration {

}
