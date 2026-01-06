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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

/**
 * 权限领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Getter
@ToString
@EqualsAndHashCode
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Permission extends BasisDomainModel implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = -5474154746791467326L;

    /**
     * 权限 ID
     */
    private Long id;

    /**
     * 权限编码
     */
    @Size(max = 50, message = "{permission.code.validation.size}")
    private String code;

    /**
     * 权限名称
     */
    @Size(max = 200, message = "{permission.name.validation.size}")
    private String name;

    /**
     * 描述
     */
    @Size(max = 500)
    private String description;

    /**
     * 有后代权限
     */
    private boolean hasDescendant;

    @Override
    public String getAuthority() {
        return code;
    }
}
