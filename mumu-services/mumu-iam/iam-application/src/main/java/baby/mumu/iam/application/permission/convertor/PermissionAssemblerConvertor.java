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

package baby.mumu.iam.application.permission.convertor;

import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionGrpcDTO;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import baby.mumu.iam.client.dto.PermissionIncludePathDownloadAllDTO.PermissionPathDTO;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.PermissionRelation;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 权限信息组装器 (Application Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class PermissionAssemblerConvertor {

    private final SimpleTextTranslation simpleTextTranslation;

    @Autowired
    public PermissionAssemblerConvertor(ObjectProvider<SimpleTextTranslation> simpleTextTranslation) {
        this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<Permission> toEntity(PermissionAddCmd permissionAddCmd) {
        return Optional.ofNullable(permissionAddCmd)
            .map(PermissionAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<Permission> toEntity(PermissionFindAllCmd permissionFindAllCmd) {
        return Optional.ofNullable(permissionFindAllCmd).map(PermissionAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Permission> toEntity(PermissionFindAllSliceCmd permissionFindAllSliceCmd) {
        return Optional.ofNullable(permissionFindAllSliceCmd)
            .map(PermissionAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "2.0.0")
    public Optional<Permission> toEntity(PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
        return Optional.ofNullable(permissionArchivedFindAllCmd)
            .map(PermissionAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Permission> toEntity(PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
        return Optional.ofNullable(permissionArchivedFindAllSliceCmd)
            .map(PermissionAssemblerMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "1.0.1")
    public void toEntity(PermissionUpdateCmd permissionUpdateCmd, @MappingTarget Permission permission) {
        Optional.ofNullable(permissionUpdateCmd).ifPresent(cmd -> PermissionAssemblerMapper.INSTANCE.toEntity(cmd,
            permission));
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<PermissionFindByIdDTO> toPermissionFindByIdDTO(Permission permission) {
        return Optional.ofNullable(permission).map(PermissionAssemblerMapper.INSTANCE::toPermissionFindByIdDTO)
            .map(permissionFindByIdCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            permissionFindByIdCo.getName()))
                    .ifPresent(permissionFindByIdCo::setName);
                return permissionFindByIdCo;
            });
    }

    @API(status = Status.STABLE, since = "2.4.0")
    public Optional<PermissionFindByCodeDTO> toPermissionFindByCodeDTO(Permission permission) {
        return Optional.ofNullable(permission).map(PermissionAssemblerMapper.INSTANCE::toPermissionFindByCodeDTO)
            .map(permissionFindByCodeCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            permissionFindByCodeCo.getName()))
                    .ifPresent(permissionFindByCodeCo::setName);
                return permissionFindByCodeCo;
            });
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public List<PermissionFindAllDTO> toPermissionFindAllDTOS(List<Permission> permissions) {
        return Optional.ofNullable(permissions).map(PermissionAssemblerMapper.INSTANCE::toPermissionFindAllDTOS)
            .orElse(new ArrayList<>());
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<PermissionFindAllSliceDTO> toPermissionFindAllSliceDTO(Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionFindAllSliceDTO)
            .map(permissionFindAllSliceCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            permissionFindAllSliceCo.getName()))
                    .ifPresent(permissionFindAllSliceCo::setName);
                return permissionFindAllSliceCo;
            });
    }

    @API(status = Status.STABLE, since = "2.0.0")
    public Optional<PermissionArchivedFindAllDTO> toPermissionArchivedFindAllDTO(
        Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionArchivedFindAllDTO)
            .map(permissionArchivedFindAllCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            permissionArchivedFindAllCo.getName()))
                    .ifPresent(permissionArchivedFindAllCo::setName);
                return permissionArchivedFindAllCo;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<PermissionArchivedFindAllSliceDTO> toPermissionArchivedFindAllSliceDTO(
        Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionArchivedFindAllSliceDTO)
            .map(permissionArchivedFindAllSliceCo -> {
                Optional.ofNullable(simpleTextTranslation).flatMap(
                        simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                            permissionArchivedFindAllSliceCo.getName()))
                    .ifPresent(permissionArchivedFindAllSliceCo::setName);
                return permissionArchivedFindAllSliceCo;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<PermissionFindAllCmd> toPermissionFindAllCmd(
        PermissionFindAllGrpcCmd permissionFindAllGrpcCmd) {
        return Optional.ofNullable(permissionFindAllGrpcCmd)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionFindAllCmd).map(permissionFindAllCmd -> {
                if (permissionFindAllCmd.getCurrent() == null) {
                    permissionFindAllCmd.setCurrent(1);
                }
                if (permissionFindAllCmd.getPageSize() == null) {
                    permissionFindAllCmd.setPageSize(10);
                }
                return permissionFindAllCmd;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<PermissionGrpcDTO> toPermissionGrpcDTO(
        PermissionFindAllDTO permissionFindAllDTO) {
        return Optional.ofNullable(permissionFindAllDTO)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionGrpcDTO);
    }

    @API(status = Status.STABLE, since = "2.3.0")
    public Optional<PermissionFindByIdGrpcDTO> toPermissionFindByIdGrpcDTO(
        PermissionFindByIdDTO permissionFindByIdDTO) {
        return Optional.ofNullable(permissionFindByIdDTO)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionFindByIdGrpcDTO);
    }

    @API(status = Status.STABLE, since = "2.3.0")
    public Optional<PermissionFindRootDTO> toPermissionFindRootDTO(
        Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionFindRootDTO);
    }

    @API(status = Status.STABLE, since = "2.3.0")
    public Optional<PermissionFindDirectDTO> toPermissionFindDirectDTO(
        Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionFindDirectDTO);
    }

    @API(status = Status.STABLE, since = "2.4.0")
    public Optional<PermissionDownloadAllDTO> toPermissionDownloadAllDTO(
        Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionDownloadAllDTO);
    }

    @API(status = Status.STABLE, since = "2.6.0")
    public Optional<PermissionIncludePathDownloadAllDTO> toPermissionIncludePathDownloadAllDTO(
        Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionIncludePathDownloadAllDTO);
    }

    @API(status = Status.STABLE, since = "2.6.0")
    public Optional<PermissionPathDTO> toPermissionPathDTO(PermissionRelation permissionRelation) {
        return Optional.ofNullable(permissionRelation)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionPathDTO);
    }

    @API(status = Status.STABLE, since = "2.13.0")
    public Optional<PermissionUpdatedDataDTO> toPermissionUpdatedDataDTO(
        Permission permission) {
        return Optional.ofNullable(permission)
            .map(PermissionAssemblerMapper.INSTANCE::toPermissionUpdatedDataDTO);
    }
}


