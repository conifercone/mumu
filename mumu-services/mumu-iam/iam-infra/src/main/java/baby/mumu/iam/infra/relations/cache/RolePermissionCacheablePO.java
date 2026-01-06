/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.iam.infra.relations.cache;

import baby.mumu.basis.enums.CacheLevelEnum;
import baby.mumu.basis.po.jpa.JpaCacheableBasisDefaultPersistentObject;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serial;
import java.util.List;

/**
 * 角色权限关系缓存
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
@Document(value = "mumu:iam:role-permission")
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionCacheablePO extends JpaCacheableBasisDefaultPersistentObject {

    @Serial
    private static final long serialVersionUID = 4744706662530961684L;

    public RolePermissionCacheablePO(Long roleId, List<Long> permissionIds) {
        this.roleId = roleId;
        this.permissionIds = permissionIds;
    }

    @Id
    @Indexed
    private Long roleId;

    @Indexed
    private List<Long> permissionIds;

    /**
     * 存活时间
     * <p>低等级别变化数据：默认缓存时间为6小时</p>
     */
    @TimeToLive
    private Long ttl = CacheLevelEnum.LOW.getSecondTtl();
}
