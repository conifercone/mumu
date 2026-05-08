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

package baby.mumu.iam.application.role.convertor;

import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.iam.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.RoleFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleGrpcDTO;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import baby.mumu.iam.domain.role.Role;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 角色信息转换器 (Application Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class RoleConvertor {

    private final SimpleTextTranslation simpleTextTranslation;

    @Autowired
    public RoleConvertor(ObjectProvider<SimpleTextTranslation> simpleTextTranslation) {
        this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<Role> toEntity(RoleAddCmd roleAddCmd) {
        return Optional.ofNullable(roleAddCmd)
            .map(RoleAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<Role> toEntity(RoleFindAllCmd roleFindAllCmd) {
        return Optional.ofNullable(roleFindAllCmd).map(RoleAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Role> toEntity(RoleFindAllSliceCmd roleFindAllSliceCmd) {
        return Optional.ofNullable(roleFindAllSliceCmd)
            .map(RoleAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Role> toEntity(RoleArchivedFindAllCmd roleArchivedFindAllCmd) {
        return Optional.ofNullable(roleArchivedFindAllCmd)
            .map(RoleAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Role> toEntity(RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd) {
        return Optional.ofNullable(roleArchivedFindAllSliceCmd)
            .map(RoleAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "1.0.1")
    public void toEntity(RoleUpdateCmd roleUpdateCmd, @MappingTarget Role role) {
        Optional.ofNullable(roleUpdateCmd).ifPresent(cmd -> RoleAssemblerMapper.INSTANCE.toEntity(cmd, role));
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<RoleFindAllDTO> toRoleFindAllDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleFindAllDTO)
            .map(roleFindAllCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            roleFindAllCo.getName()))
                    .ifPresent(roleFindAllCo::setName);
                return roleFindAllCo;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<RoleFindAllSliceDTO> toRoleFindAllSliceDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleFindAllSliceDTO)
            .map(roleFindAllSliceCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            roleFindAllSliceCo.getName()))
                    .ifPresent(roleFindAllSliceCo::setName);
                return roleFindAllSliceCo;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<RoleArchivedFindAllDTO> toRoleArchivedFindAllDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleArchivedFindAllDTO)
            .map(roleArchivedFindAllCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            roleArchivedFindAllCo.getName()))
                    .ifPresent(roleArchivedFindAllCo::setName);
                return roleArchivedFindAllCo;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<RoleArchivedFindAllSliceDTO> toRoleArchivedFindAllSliceDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleArchivedFindAllSliceDTO)
            .map(roleArchivedFindAllSliceCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            roleArchivedFindAllSliceCo.getName()))
                    .ifPresent(roleArchivedFindAllSliceCo::setName);
                return roleArchivedFindAllSliceCo;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<RoleFindAllCmd> toRoleFindAllCmd(RoleFindAllGrpcCmd roleFindAllGrpcCmd) {
        return Optional.ofNullable(roleFindAllGrpcCmd).map(RoleAssemblerMapper.INSTANCE::toRoleFindAllCmd)
            .map(roleFindAllCmd -> {
                if (roleFindAllCmd.getCurrent() == null) {
                    roleFindAllCmd.setCurrent(1);
                }
                if (roleFindAllCmd.getPageSize() == null) {
                    roleFindAllCmd.setPageSize(10);
                }
                return roleFindAllCmd;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<RoleGrpcDTO> toRoleGrpcDTO(RoleFindAllDTO roleFindAllDTO) {
        return Optional.ofNullable(roleFindAllDTO).map(RoleAssemblerMapper.INSTANCE::toRoleGrpcDTO)
            .map(roleFindAllGrpcCo ->
                roleFindAllGrpcCo.toBuilder().addAllPermissions(
                    Optional.ofNullable(roleFindAllDTO.getPermissions()).map(
                        permissions -> permissions.stream()
                            .map(RoleAssemblerMapper.INSTANCE::toRolePermissionGrpcDTO)
                            .collect(java.util.stream.Collectors.toList())).orElse(new java.util.ArrayList<>())).build()
            );
    }

    @API(status = Status.STABLE, since = "2.4.0")
    public Optional<RoleFindByIdDTO> toRoleFindByIdDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleFindByIdDTO);
    }

    @API(status = Status.STABLE, since = "2.5.0")
    public Optional<RoleFindByCodeDTO> toRoleFindByCodeDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleFindByCodeDTO);
    }

    @API(status = Status.STABLE, since = "2.4.0")
    public Optional<RoleFindRootDTO> toRoleFindRootDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleFindRootDTO);
    }

    @API(status = Status.STABLE, since = "2.4.0")
    public Optional<RoleFindDirectDTO> toRoleFindDirectDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleFindDirectDTO);
    }

    @API(status = Status.STABLE, since = "2.4.0")
    public Optional<RoleFindByIdGrpcDTO> toRoleFindByIdGrpcDTO(RoleFindByIdDTO roleFindByIdDTO) {
        return Optional.ofNullable(roleFindByIdDTO).map(RoleAssemblerMapper.INSTANCE::toRoleFindByIdGrpcDTO);
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<RoleUpdatedDataDTO> toRoleUpdatedDataDTO(Role role) {
        return Optional.ofNullable(role).map(RoleAssemblerMapper.INSTANCE::toRoleUpdatedDataDTO);
    }
}
