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

/**
 * 分布式锁顶级接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface DistributedLock {

  /**
   * 尝试获取锁
   *
   * @param lockName 锁名称
   * @return 是否成功获取到分布式锁
   */
  boolean tryLock(String lockName);

  /**
   * 尝试获取锁
   *
   * @param lockName 锁名称
   * @param waitTime 等待时间，单位毫秒
   * @return 是否成功获取到分布式锁
   */
  boolean tryLock(String lockName, long waitTime);

  /**
   * 释放锁
   *
   * @param lockName 锁名称
   */
  void unlock(String lockName);
}
