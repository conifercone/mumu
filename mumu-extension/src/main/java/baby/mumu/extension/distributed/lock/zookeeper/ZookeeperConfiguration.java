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
package baby.mumu.extension.distributed.lock.zookeeper;

import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.distributed.lock.DistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zookeeper配置文件
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "mumu.extension.distributed.lock.zookeeper", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(ExtensionProperties.class)
public class ZookeeperConfiguration {

  private final ExtensionProperties extensionProperties;

  @Autowired
  public ZookeeperConfiguration(ExtensionProperties extensionProperties) {
    this.extensionProperties = extensionProperties;
  }

  @Bean
  @ConditionalOnMissingBean(CuratorFramework.class)
  public CuratorFramework mumuCuratorFramework() {
    ZookeeperProperties zookeeper = extensionProperties.getDistributed().getLock().getZookeeper();
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
      zookeeper.getConnectString(),
      zookeeper.getSessionTimeoutMs(),
      zookeeper.getConnectionTimeoutMs(),
      new RetryNTimes(zookeeper.getRetryCount(), zookeeper.getElapsedTimeMs()));
    curatorFramework.start();
    return curatorFramework;
  }

  @Bean
  @ConditionalOnMissingBean(DistributedLock.class)
  @ConditionalOnBean({CuratorFramework.class, ZookeeperProperties.class})
  public DistributedLock zookeeperDistributedLock(CuratorFramework mumuCuratorFramework,
    ZookeeperProperties zookeeperProperties) {
    return new ZookeeperDistributedLockImpl(mumuCuratorFramework, zookeeperProperties);
  }
}
