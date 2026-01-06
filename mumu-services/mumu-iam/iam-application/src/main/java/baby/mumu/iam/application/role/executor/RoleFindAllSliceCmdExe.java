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

package baby.mumu.iam.application.role.executor;

import baby.mumu.iam.client.cmds.RoleFindAllSliceCmd;
import baby.mumu.iam.client.dto.RoleFindAllSliceDTO;
import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.domain.role.gateway.RoleGateway;
import baby.mumu.iam.infra.role.convertor.RoleConvertor;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 角色查询指令执行器（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "RoleFindAllSliceCmdExe")
public class RoleFindAllSliceCmdExe {

    private final RoleGateway roleGateway;
    private final RoleConvertor roleConvertor;

    @Autowired
    public RoleFindAllSliceCmdExe(RoleGateway roleGateway, RoleConvertor roleConvertor) {
        this.roleGateway = roleGateway;
        this.roleConvertor = roleConvertor;
    }

    public Slice<RoleFindAllSliceDTO> execute(@NonNull RoleFindAllSliceCmd roleFindAllSliceCmd) {
        Role role = roleConvertor.toEntity(roleFindAllSliceCmd).orElseGet(Role::new);
        Slice<Role> roles = roleGateway.findAllSlice(role,
            roleFindAllSliceCmd.getCurrent(), roleFindAllSliceCmd.getPageSize());
        List<RoleFindAllSliceDTO> roleFindAllSliceDTOS = roles.getContent().stream()
            .map(roleConvertor::toRoleFindAllSliceDTO)
            .filter(Optional::isPresent).map(Optional::get).toList();
        return new SliceImpl<>(roleFindAllSliceDTOS, roles.getPageable(),
            roles.hasNext());
    }
}
