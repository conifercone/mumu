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

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.distributed.lock.DistributedLock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * zookeeper分布式锁实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class ZookeeperDistributedLockImpl implements DistributedLock {

  private static final Logger log = LogManager.getLogger(ZookeeperDistributedLockImpl.class);
  public static final String PATH_SEPARATOR = "/";
  private final CuratorFramework curatorFramework;
  private final String rootNode;
  private final Map<String, InterProcessLock> LOCKS = new ConcurrentHashMap<>();

  public ZookeeperDistributedLockImpl(CuratorFramework mumuCuratorFramework,
    @NotNull ZookeeperProperties zookeeperProperties) {
    this.curatorFramework = mumuCuratorFramework;
    rootNode = ensureLeadingAndTrailingSlash(zookeeperProperties.getRootLockPath());
  }

  private static @NotNull String ensureLeadingAndTrailingSlash(String input) {
    if (input == null) {
      return PATH_SEPARATOR;
    }
    if (!input.startsWith(PATH_SEPARATOR)) {
      input = PATH_SEPARATOR + input;
    }
    if (!input.endsWith(PATH_SEPARATOR)) {
      input = input + PATH_SEPARATOR;
    }
    return input;
  }

  @Override
  public boolean tryLock(String lockName) {
    try {
      return getInterProcessLock(lockName).acquire(-1, null);
    } catch (Exception e) {
      log.error(ResponseCode.FAILED_TO_OBTAIN_DISTRIBUTED_LOCK.getMessage(), e);
      return false;
    }
  }

  private InterProcessLock getInterProcessLock(String lockName) {
    if (StringUtils.isBlank(lockName)) {
      throw new MuMuException(ResponseCode.FAILED_TO_OBTAIN_DISTRIBUTED_LOCK);
    }
    if (lockName.startsWith(PATH_SEPARATOR)) {
      lockName = lockName.substring(1);
    }
    // 使用 computeIfAbsent 来确保每个锁名只有一个锁实例
    return LOCKS.computeIfAbsent(lockName,
      name -> new InterProcessMutex(curatorFramework, rootNode + name));
  }

  @Override
  public boolean tryLock(String lockName, long waitTime) {
    try {
      return getInterProcessLock(lockName).acquire(waitTime, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      log.error(ResponseCode.FAILED_TO_OBTAIN_DISTRIBUTED_LOCK.getMessage(), e);
      return false;
    }
  }

  @Override
  public void unlock(String lockName) {
    try {
      InterProcessLock lock = getInterProcessLock(lockName);
      // 确保只有当前线程持有锁时才释放
      if (lock.isAcquiredInThisProcess()) {
        lock.release();
        log.info("Lock released successfully for: {}", lockName);
      } else {
        log.warn("Lock {} was not acquired by the current process, skip releasing.", lockName);
      }
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FAILED_TO_RELEASE_DISTRIBUTED_LOCK);
    }
  }
}
