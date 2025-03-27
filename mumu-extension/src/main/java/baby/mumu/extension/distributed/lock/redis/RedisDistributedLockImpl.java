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

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.distributed.lock.DistributedLock;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;

/**
 * redis分布式锁实现
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.9.0
 */
public class RedisDistributedLockImpl implements DistributedLock {

  private static final Logger log = LogManager.getLogger(RedisDistributedLockImpl.class);
  private final RLock lock;
  private final long leaseTime = 60000;

  public RedisDistributedLockImpl(RLock lock) {
    this.lock = lock;
  }

  @Override
  public void tryLock() {
    try {
      lock.lock(leaseTime, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      log.error(ResponseCode.FAILED_TO_OBTAIN_DISTRIBUTED_LOCK.getMessage(), e);
    }
  }

  @Override
  public boolean tryLock(long waitTime) {
    try {
      return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      log.error(ResponseCode.FAILED_TO_OBTAIN_DISTRIBUTED_LOCK.getMessage(), e);
      return false;
    }
  }

  @Override
  public void unlock() {
    try {
      lock.unlock();
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FAILED_TO_RELEASE_DISTRIBUTED_LOCK);
    }
  }
}
