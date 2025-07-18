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

package baby.mumu.iam.infra.role.gatewayimpl.cache.po;

import baby.mumu.basis.enums.CacheLevelEnum;
import baby.mumu.basis.po.jpa.JpaCacheableBasisArchivablePersistentObject;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 角色基本信息缓存
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(value = "mumu:iam:role")
public class RoleCacheablePO extends JpaCacheableBasisArchivablePersistentObject {

  @Serial
  private static final long serialVersionUID = 2814267592168109003L;

  /**
   * 角色ID
   */
  @Id
  @Indexed
  private Long id;

  /**
   * 角色编码
   */
  @Indexed
  private String code;

  /**
   * 角色名称
   */
  private String name;

  private String description;

  /**
   * 存活时间
   * <p>低等级别变化数据：默认缓存时间为6小时</p>
   */
  @TimeToLive
  private Long ttl = CacheLevelEnum.LOW.getSecondTtl();
}
