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

package com.sky.centaur.extension;

import com.sky.centaur.extension.cors.CentaurCorsConfiguration;
import com.sky.centaur.extension.distributed.lock.zookeeper.ZookeeperConfiguration;
import com.sky.centaur.extension.processor.grpc.GrpcExceptionAdvice;
import com.sky.centaur.extension.processor.response.ResponseBodyProcessor;
import com.sky.centaur.extension.sql.DatasourceConfiguration;
import com.sky.centaur.extension.translation.TranslationConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 拓展模块配置
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
@Import({GrpcExceptionAdvice.class, ResponseBodyProcessor.class,
    ZookeeperConfiguration.class, CentaurCorsConfiguration.class, DatasourceConfiguration.class,
    TranslationConfiguration.class})
@EnableConfigurationProperties(ExtensionProperties.class)
public class ExtensionConfiguration {

}
