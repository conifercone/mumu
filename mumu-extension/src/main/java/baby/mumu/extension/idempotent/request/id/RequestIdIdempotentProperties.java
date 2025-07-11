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

package baby.mumu.extension.idempotent.request.id;

import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Request ID 幂等配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Data
public class RequestIdIdempotentProperties {

  private boolean enabled;

  @NestedConfigurationProperty
  private RedisRequestIdIdempotentProperties redis = new RedisRequestIdIdempotentProperties();

  @Data
  public static class RedisRequestIdIdempotentProperties {

    private boolean enabled;

    private Long timeout = 5L;

    private TimeUnit unit = TimeUnit.MINUTES;
  }
}
