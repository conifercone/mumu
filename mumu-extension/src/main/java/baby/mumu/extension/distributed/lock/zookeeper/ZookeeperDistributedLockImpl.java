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
package baby.mumu.extension.distributed.lock.zookeeper;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.distributed.lock.DistributedLock;
import org.apache.curator.framework.recipes.locks.InterProcessLock;

/**
 * zookeeper分布式锁实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class ZookeeperDistributedLockImpl implements DistributedLock {

  private final InterProcessLock interProcessLock;

  public ZookeeperDistributedLockImpl(InterProcessLock interProcessLock) {
    this.interProcessLock = interProcessLock;
  }

  @Override
  public void lock() {
    try {
      interProcessLock.acquire();
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FAILED_TO_OBTAIN_DISTRIBUTED_LOCK);
    }
  }

  @Override
  public void unlock() {
    try {
      interProcessLock.release();
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FAILED_TO_RELEASE_DISTRIBUTED_LOCK);
    }
  }
}
