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
package baby.mumu.extension.distributed.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁顶级接口
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface DistributedLock {

  /**
   * 尝试获取锁
   *
   * @return 是否成功获取到分布式锁
   */
  boolean tryLock();

  /**
   * 尝试获取锁
   *
   * @param unit     等待时间单位
   * @param waitTime 等待时间
   * @return 是否成功获取到分布式锁
   */
  boolean tryLock(long waitTime, TimeUnit unit);

  /**
   * 释放锁
   */
  void unlock();
}
