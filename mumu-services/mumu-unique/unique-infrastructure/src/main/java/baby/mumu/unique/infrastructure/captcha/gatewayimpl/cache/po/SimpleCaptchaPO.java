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

package baby.mumu.unique.infrastructure.captcha.gatewayimpl.cache.po;

import baby.mumu.basis.enums.CacheLevelEnum;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.TextIndexed;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

/**
 * simple captcha redis数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Data
@Document(value = "mumu:unique:simple-captcha")
public class SimpleCaptchaPO {

  @Id
  @Indexed
  private Long id;

  /**
   * 验证码内容
   */
  @TextIndexed
  private String target;

  /**
   * 存活时间
   */
  @TimeToLive
  private Long ttl = CacheLevelEnum.MEDIUM.getSecondTtl();
}
