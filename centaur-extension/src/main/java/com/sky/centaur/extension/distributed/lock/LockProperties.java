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

package com.sky.centaur.extension.distributed.lock;

import com.sky.centaur.extension.distributed.lock.zookeeper.ZookeeperProperties;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 分布式锁配置属性
 *
 * @author kaiyu.shan
 * @since 2024-03-06
 */
@Data
public class LockProperties {

  /**
   * zookeeper相关配置
   */
  @NestedConfigurationProperty
  private ZookeeperProperties zookeeper;
}
