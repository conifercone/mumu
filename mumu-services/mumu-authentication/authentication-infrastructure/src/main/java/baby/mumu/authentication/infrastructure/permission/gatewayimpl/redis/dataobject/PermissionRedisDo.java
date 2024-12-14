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
package baby.mumu.authentication.infrastructure.permission.gatewayimpl.redis.dataobject;

import baby.mumu.basis.dataobject.jpa.JpaRedisBasisArchivableDataObject;
import baby.mumu.basis.enums.CacheLevelEnum;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 权限缓存数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(value = "permission")
public class PermissionRedisDo extends JpaRedisBasisArchivableDataObject {

  @Serial
  private static final long serialVersionUID = -6055203641258590170L;

  /**
   * 权限ID
   */
  @Id
  @Indexed
  private Long id;

  /**
   * 权限编码
   */
  @Indexed
  private String code;

  /**
   * 权限名称
   */
  private String name;

  private String description;

  /**
   * 有后代权限
   */
  private boolean hasDescendant;

  /**
   * 存活时间
   * <p>低等级别变化数据：默认缓存时间为6小时</p>
   */
  @TimeToLive
  private Long ttl = CacheLevelEnum.LOW.getSecondTtl();
}
