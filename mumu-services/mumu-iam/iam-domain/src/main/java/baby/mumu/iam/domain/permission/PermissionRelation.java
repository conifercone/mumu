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

package baby.mumu.iam.domain.permission;

import baby.mumu.basis.domain.BasisDomainModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * 权限关系领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.6.0
 */
@Getter
@ToString
@EqualsAndHashCode
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class PermissionRelation extends BasisDomainModel {

    @Serial
    private static final long serialVersionUID = 8474154746791467326L;

    /**
     * 祖先权限 ID
     */
    private Long ancestorId;

    /**
     * 后代权限 ID
     */
    private Long descendantId;

    /**
     * 深度
     */
    private Long depth;
}
