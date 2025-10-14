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

package baby.mumu.iam.infra.permission.convertor;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionGrpcDTO;
import baby.mumu.iam.client.cmds.PermissionAddCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionUpdateCmd;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllDTO;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionDownloadAllDTO;
import baby.mumu.iam.client.dto.PermissionFindAllDTO;
import baby.mumu.iam.client.dto.PermissionFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionFindByCodeDTO;
import baby.mumu.iam.client.dto.PermissionFindByIdDTO;
import baby.mumu.iam.client.dto.PermissionFindDirectDTO;
import baby.mumu.iam.client.dto.PermissionFindRootDTO;
import baby.mumu.iam.client.dto.PermissionIncludePathDownloadAllDTO;
import baby.mumu.iam.client.dto.PermissionUpdatedDataDTO;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.infra.permission.gatewayimpl.cache.po.PermissionCacheablePO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.PermissionArchivedRepository;
import baby.mumu.iam.infra.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionArchivedPO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.iam.infra.relations.database.PermissionPathPO;
import baby.mumu.iam.infra.relations.database.PermissionPathRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 权限信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class PermissionConvertor {

  private final PermissionRepository permissionRepository;
  private final SimpleTextTranslation simpleTextTranslation;
  private final PermissionArchivedRepository permissionArchivedRepository;
  private final PermissionPathRepository permissionPathRepository;

  @Autowired
  public PermissionConvertor(PermissionRepository permissionRepository,
    ObjectProvider<SimpleTextTranslation> simpleTextTranslation,
    PermissionArchivedRepository permissionArchivedRepository,
    PermissionPathRepository permissionPathRepository) {
    this.permissionRepository = permissionRepository;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    this.permissionArchivedRepository = permissionArchivedRepository;
    this.permissionPathRepository = permissionPathRepository;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Permission> toEntity(PermissionPO permissionPO) {
    return Optional.ofNullable(permissionPO).map(
      PermissionMapper.INSTANCE::toEntity).flatMap(this::hasDescendant);
  }

  @API(status = Status.STABLE, since = "2.14.0")
  public List<Permission> toEntities(List<PermissionPO> permissionPOList) {
    List<Permission> permissions = Optional.ofNullable(permissionPOList).map(
      PermissionMapper.INSTANCE::toEntities).orElse(new ArrayList<>());
    return this.hasDescendant(permissions);
  }

  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<Permission> toEntityDoNotJudgeHasDescendant(PermissionPO permissionPO) {
    return Optional.ofNullable(permissionPO).map(
      PermissionMapper.INSTANCE::toEntity);
  }

  private Optional<Permission> hasDescendant(Permission permission) {
    return Optional.ofNullable(permission).map(permissionNotNull -> {
      permissionNotNull.setHasDescendant(
        permissionPathRepository.existsDescendantPermissions(permission.getId()));
      return permissionNotNull;
    });
  }

  private List<Permission> hasDescendant(List<Permission> permissions) {
    List<Long> permissionIds = Optional.ofNullable(permissions).orElse(new ArrayList<>()).stream()
      .map(Permission::getId).toList();
    if (permissionIds.isEmpty()) {
      return permissions;
    }
    Set<Long> ancestorIdsWithDescendants = new HashSet<>(
      permissionPathRepository.findAncestorIdsWithDescendants(
        permissionIds));
    permissions.forEach(p -> {
      if (ancestorIdsWithDescendants.contains(p.getId())) {
        p.setHasDescendant(true);
      }
    });
    return permissions;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<PermissionPO> toPermissionPO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPermissionPO);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Permission> toEntity(PermissionAddCmd permissionAddCmd) {
    return Optional.ofNullable(permissionAddCmd)
      .map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Permission> toEntity(PermissionUpdateCmd permissionUpdateCmd) {
    return Optional.ofNullable(permissionUpdateCmd).map(permissionUpdateCmdNotNull -> {
      if (permissionUpdateCmdNotNull.getId() == null) {
        throw new ApplicationException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY);
      }
      return permissionRepository.findById(
          permissionUpdateCmdNotNull.getId()).flatMap(this::toEntity)
        .map(permission -> {
          String codeBeforeUpdate = permission.getCode();
          PermissionMapper.INSTANCE.toEntity(permissionUpdateCmdNotNull, permission);
          String codeAfterUpdate = permission.getCode();
          if (StringUtils.isNotBlank(codeAfterUpdate) && !codeAfterUpdate.equals(codeBeforeUpdate)
            && (permissionRepository.existsByCode(
            codeAfterUpdate) || permissionArchivedRepository.existsByCode(
            codeAfterUpdate))) {
            throw new ApplicationException(ResponseCode.PERMISSION_CODE_ALREADY_EXISTS);
          }
          return permission;
        }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Permission> toEntity(PermissionFindAllCmd permissionFindAllCmd) {
    return Optional.ofNullable(permissionFindAllCmd).map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Permission> toEntity(PermissionFindAllSliceCmd permissionFindAllSliceCmd) {
    return Optional.ofNullable(permissionFindAllSliceCmd)
      .map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<Permission> toEntity(PermissionArchivedPO permissionArchivedPO) {
    return Optional.ofNullable(permissionArchivedPO).map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public List<Permission> toEntitiesFromArchivedPO(
    List<PermissionArchivedPO> permissionArchivedPOList) {
    return Optional.ofNullable(permissionArchivedPOList)
      .map(PermissionMapper.INSTANCE::toEntitiesFromArchivedPO).orElse(new ArrayList<>());
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Permission> toEntity(PermissionCacheablePO permissionCacheablePO) {
    return Optional.ofNullable(permissionCacheablePO).map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionCacheablePO> toPermissionCacheablePO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPermissionCacheablePO);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<Permission> toEntity(
    @Validated PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
    return Optional.ofNullable(permissionArchivedFindAllCmd)
      .map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Permission> toEntity(
    @Validated PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
    return Optional.ofNullable(permissionArchivedFindAllSliceCmd)
      .map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<PermissionFindByIdDTO> toPermissionFindByIdDTO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPermissionFindByIdDTO)
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
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPermissionFindByCodeDTO)
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
    return Optional.ofNullable(permissions).map(PermissionMapper.INSTANCE::toPermissionFindAllDTOS)
      .orElse(new ArrayList<>());
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionFindAllSliceDTO> toPermissionFindAllSliceDTO(Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionFindAllSliceDTO)
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
      .map(PermissionMapper.INSTANCE::toPermissionArchivedFindAllDTO)
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
      .map(PermissionMapper.INSTANCE::toPermissionArchivedFindAllSliceDTO)
      .map(permissionArchivedFindAllSliceCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionArchivedFindAllSliceCo.getName()))
          .ifPresent(permissionArchivedFindAllSliceCo::setName);
        return permissionArchivedFindAllSliceCo;
      });
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<PermissionArchivedPO> toPermissionArchivedPO(PermissionPO permissionPO) {
    return Optional.ofNullable(permissionPO).map(PermissionMapper.INSTANCE::toPermissionArchivedPO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionArchivedPO> toPermissionArchivedPO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPermissionArchivedPO);
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<PermissionPO> toPermissionPO(PermissionArchivedPO permissionArchivedPO) {
    return Optional.ofNullable(permissionArchivedPO).map(PermissionMapper.INSTANCE::toPermissionPO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionFindAllCmd> toPermissionFindAllCmd(
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd) {
    return Optional.ofNullable(permissionFindAllGrpcCmd)
      .map(PermissionMapper.INSTANCE::toPermissionFindAllCmd).map(permissionFindAllCmd -> {
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
      .map(PermissionMapper.INSTANCE::toPermissionGrpcDTO);
  }

  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindByIdGrpcDTO> toPermissionFindByIdGrpcDTO(
    PermissionFindByIdDTO permissionFindByIdDTO) {
    return Optional.ofNullable(permissionFindByIdDTO)
      .map(PermissionMapper.INSTANCE::toPermissionFindByIdGrpcDTO);
  }

  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindRootDTO> toPermissionFindRootDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionFindRootDTO);
  }

  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindDirectDTO> toPermissionFindDirectDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionFindDirectDTO);
  }

  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<PermissionDownloadAllDTO> toPermissionDownloadAllDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionDownloadAllDTO);
  }

  @API(status = Status.STABLE, since = "2.6.0")
  public Optional<PermissionIncludePathDownloadAllDTO> toPermissionIncludePathDownloadAllDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionIncludePathDownloadAllDTO)
      .map(permissionIncludePathDownloadAllDTO -> {
        if (permissionIncludePathDownloadAllDTO.isHasDescendant()) {
          List<PermissionPathPO> byAncestorIdIn = permissionPathRepository.findByAncestorIdIn(
            Collections.singleton(permissionIncludePathDownloadAllDTO.getId()));
          permissionIncludePathDownloadAllDTO.setDescendants(
            byAncestorIdIn.stream().map(PermissionPathPO::getId)
              .map(PermissionMapper.INSTANCE::toPermissionPathDTO)
              .collect(Collectors.toList()));
        }
        return permissionIncludePathDownloadAllDTO;
      });
  }

  @API(status = Status.STABLE, since = "2.13.0")
  public Optional<PermissionUpdatedDataDTO> toPermissionUpdatedDataDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionUpdatedDataDTO);
  }
}
