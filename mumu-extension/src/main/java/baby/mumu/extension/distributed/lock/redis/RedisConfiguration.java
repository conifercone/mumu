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
package baby.mumu.extension.distributed.lock.redis;

import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.distributed.lock.DistributedLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redis配置文件
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.9.0
 */
@Configuration
@ConditionalOnProperty(prefix = "mumu.extension.distributed.lock.redis", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(ExtensionProperties.class)
public class RedisConfiguration {

  private final ExtensionProperties extensionProperties;

  @Autowired
  public RedisConfiguration(ExtensionProperties extensionProperties) {
    this.extensionProperties = extensionProperties;
  }

  @Bean
  @ConditionalOnMissingBean(DistributedLock.class)
  @ConditionalOnBean(RedissonClient.class)
  public DistributedLock redisDistributedLockImpl(RedissonClient redisson) {
    return new RedisDistributedLockImpl(
      redisson.getLock(extensionProperties.getDistributed().getLock().getRedis().getLockName()));
  }
}
