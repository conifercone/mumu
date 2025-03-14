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
package baby.mumu.basis.enums;

import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationUtils;

/**
 * 缓存等级枚举
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.4.0
 */
public enum CacheLevelEnum {
  HIGH(5, TimeUnit.SECONDS),
  MEDIUM(1, TimeUnit.HOURS),
  LOW(6, TimeUnit.HOURS);


  private final long ttl;
  private final TimeUnit ttlUnit;

  CacheLevelEnum(long ttl, TimeUnit ttlUnit) {
    this.ttl = ttl;
    this.ttlUnit = ttlUnit;
  }

  public long getSecondTtl() {
    return DurationUtils.toDuration(ttl, ttlUnit).getSeconds();
  }
}
