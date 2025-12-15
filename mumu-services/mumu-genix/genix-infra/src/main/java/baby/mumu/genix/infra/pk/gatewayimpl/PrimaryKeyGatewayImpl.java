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

package baby.mumu.genix.infra.pk.gatewayimpl;

import baby.mumu.genix.domain.pk.gateway.PrimaryKeyGateway;
import me.ahoo.cosid.snowflake.SnowflakeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 唯一性主键领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class PrimaryKeyGatewayImpl implements PrimaryKeyGateway {

  private final SnowflakeId snowflakeId;

  @Autowired
  public PrimaryKeyGatewayImpl(
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") @Qualifier("__share__SnowflakeId") @Lazy SnowflakeId snowflakeId) {
    this.snowflakeId = snowflakeId;
  }

  @Override
  public long snowflake() {
    return snowflakeId.generate();
  }
}
