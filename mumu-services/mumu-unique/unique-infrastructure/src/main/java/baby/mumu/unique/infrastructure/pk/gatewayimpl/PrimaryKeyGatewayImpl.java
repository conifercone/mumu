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
package baby.mumu.unique.infrastructure.pk.gatewayimpl;

import baby.mumu.unique.domain.pk.gateway.PrimaryKeyGateway;
import com.github.guang19.leaf.core.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 唯一性主键领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class PrimaryKeyGatewayImpl implements PrimaryKeyGateway {

  private final IdGenerator snowflakeIdGenerator;

  @Autowired
  public PrimaryKeyGatewayImpl(IdGenerator snowflakeIdGenerator) {
    this.snowflakeIdGenerator = snowflakeIdGenerator;
  }

  @Override
  public long snowflake() {
    return snowflakeIdGenerator.nextId().getId();
  }
}
