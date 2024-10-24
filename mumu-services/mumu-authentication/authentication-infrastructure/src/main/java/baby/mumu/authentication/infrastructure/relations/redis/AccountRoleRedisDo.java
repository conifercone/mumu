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
package baby.mumu.authentication.infrastructure.relations.redis;

import baby.mumu.basis.dataobject.jpa.JpaRedisBasisDefaultDataObject;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import java.io.Serial;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 账户角色关系缓存
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(value = "account-role")
@AllArgsConstructor
@NoArgsConstructor
public class AccountRoleRedisDo extends JpaRedisBasisDefaultDataObject {

  @Serial
  private static final long serialVersionUID = 1872502889758524323L;

  public AccountRoleRedisDo(Long userId, List<Long> roleIds) {
    this.userId = userId;
    this.roleIds = roleIds;
  }

  @Id
  @Indexed
  private Long userId;

  @Indexed
  private List<Long> roleIds;

  /**
   * 存活时间
   * <p>低等级别变化数据：默认缓存时间为6小时</p>
   */
  @TimeToLive(unit = TimeUnit.HOURS)
  private Long ttl = 6L;
}
