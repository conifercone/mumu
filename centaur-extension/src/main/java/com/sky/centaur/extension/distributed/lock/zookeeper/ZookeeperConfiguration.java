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

package com.sky.centaur.extension.distributed.lock.zookeeper;

import com.sky.centaur.extension.ExtensionProperties;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
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
@ConditionalOnProperty(prefix = "centaur.extension.distributed.lock.zookeeper", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(ExtensionProperties.class)
public class ZookeeperConfiguration {

  private final ExtensionProperties extensionProperties;

  @Autowired
  public ZookeeperConfiguration(ExtensionProperties extensionProperties) {
    this.extensionProperties = extensionProperties;
  }

  @Bean
  public InterProcessLock centaurInterProcessLock() {
    ZookeeperProperties zookeeper = extensionProperties.getDistributed().getLock().getZookeeper();
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
        zookeeper.getConnectString(),
        zookeeper.getSessionTimeoutMs(),
        zookeeper.getConnectionTimeoutMs(),
        new RetryNTimes(zookeeper.getRetryCount(), zookeeper.getElapsedTimeMs()));
    curatorFramework.start();
    return new InterProcessMutex(curatorFramework, "/locks");
  }

  @Bean
  public DistributedLock zookeeperDistributedLock(InterProcessLock centaurInterProcessLock) {
    return new ZookeeperDistributedLockImpl(centaurInterProcessLock);
  }
}
