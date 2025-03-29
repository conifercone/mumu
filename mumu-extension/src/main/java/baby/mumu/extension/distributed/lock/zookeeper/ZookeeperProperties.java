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

import lombok.Data;

/**
 * zookeeper配置属性
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
public class ZookeeperProperties {

  /**
   * 重试次数
   */
  private int retryCount = 1;

  /**
   * 重试间隔时间
   */
  private int elapsedTimeMs = 2000;

  /**
   * zookeeper 地址
   * <p>eg:localhost:2181,localhost:2182,localhost:2183</p>
   */
  private String connectString = "localhost:2181";

  /**
   * 会话超时时间
   */
  private int sessionTimeoutMs = 60000;

  /**
   * 连接超时时间
   */
  private int connectionTimeoutMs = 10000;

  /**
   * 是否启用
   */
  private boolean enabled;

  /**
   * 分布式锁路径
   */
  private String lockPath = "/mumu/lock/resource";
}
