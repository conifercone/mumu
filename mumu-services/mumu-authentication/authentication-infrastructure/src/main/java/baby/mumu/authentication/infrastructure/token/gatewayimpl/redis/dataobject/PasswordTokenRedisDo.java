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
package baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.dataobject;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.TextIndexed;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 密码模式 token redis数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@Document(value = "password-token")
public class PasswordTokenRedisDo {

  @Id
  @Indexed
  private Long id;

  /**
   * token值
   */
  @TextIndexed
  private String tokenValue;

  /**
   * 存活时间
   */
  @TimeToLive
  private Long ttl = 5L;
}