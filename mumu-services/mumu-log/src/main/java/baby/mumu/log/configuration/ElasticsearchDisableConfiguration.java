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
package baby.mumu.log.configuration;

import org.springframework.boot.actuate.autoconfigure.data.elasticsearch.ElasticsearchReactiveHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticsearchRestHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * es禁用配置类
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "mumu.log.elasticsearch", name = "enabled", havingValue = "false")
@EnableAutoConfiguration(exclude = {
  ElasticsearchRestClientAutoConfiguration.class,
  ElasticsearchReactiveHealthContributorAutoConfiguration.class,
  ElasticsearchClientAutoConfiguration.class,
  ElasticsearchRestHealthContributorAutoConfiguration.class
})
public class ElasticsearchDisableConfiguration {

}
