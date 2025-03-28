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
package baby.mumu.authentication.infrastructure.permission.convertor;

import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcDTO;
import baby.mumu.authentication.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.authentication.client.cmds.PermissionAddCmd;
import baby.mumu.authentication.client.cmds.PermissionArchivedFindAllCmd;
import baby.mumu.authentication.client.cmds.PermissionArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.PermissionFindAllCmd;
import baby.mumu.authentication.client.cmds.PermissionFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.PermissionUpdateCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllDTO;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllSliceDTO;
import baby.mumu.authentication.client.dto.PermissionDownloadAllDTO;
import baby.mumu.authentication.client.dto.PermissionFindAllDTO;
import baby.mumu.authentication.client.dto.PermissionFindAllSliceDTO;
import baby.mumu.authentication.client.dto.PermissionFindByCodeDTO;
import baby.mumu.authentication.client.dto.PermissionFindByIdDTO;
import baby.mumu.authentication.client.dto.PermissionFindDirectDTO;
import baby.mumu.authentication.client.dto.PermissionFindRootDTO;
import baby.mumu.authentication.client.dto.PermissionIncludePathDownloadAllDTO;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.cache.po.PermissionRedisPO;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionArchivedRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.po.PermissionArchivedPO;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathPO;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathRepository;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 权限信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
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

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Permission> toEntity(PermissionPO permissionPO) {
    return Optional.ofNullable(permissionPO).map(
      PermissionMapper.INSTANCE::toEntity).flatMap(this::hasDescendant);
  }

  @Contract("_ -> new")
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

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<PermissionPO> toPO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPO);
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
        throw new MuMuException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY);
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
            throw new MuMuException(ResponseCode.PERMISSION_CODE_ALREADY_EXISTS);
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

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Permission> toEntity(PermissionRedisPO permissionRedisPO) {
    return Optional.ofNullable(permissionRedisPO).map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionRedisPO> toPermissionRedisPO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPermissionRedisPO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionPO> toPO(PermissionRedisPO permissionRedisPO) {
    return Optional.ofNullable(permissionRedisPO).map(PermissionMapper.INSTANCE::toPO);
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
  public Optional<PermissionFindByIdDTO> toFindByIdDTO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toFindByIdDTO)
      .map(permissionFindByIdCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionFindByIdCo.getName()))
          .ifPresent(permissionFindByIdCo::setName);
        return permissionFindByIdCo;
      });
  }

  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<PermissionFindByCodeDTO> toFindByCodeDTO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toFindByCodeDTO)
      .map(permissionFindByCodeCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionFindByCodeCo.getName()))
          .ifPresent(permissionFindByCodeCo::setName);
        return permissionFindByCodeCo;
      });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<PermissionFindAllDTO> toFindAllDTO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toFindAllDTO)
      .map(permissionFindAllCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionFindAllCo.getName()))
          .ifPresent(permissionFindAllCo::setName);
        return permissionFindAllCo;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionFindAllSliceDTO> toFindAllSliceDTO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toFindAllSliceDTO)
      .map(permissionFindAllSliceCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionFindAllSliceCo.getName()))
          .ifPresent(permissionFindAllSliceCo::setName);
        return permissionFindAllSliceCo;
      });
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<PermissionArchivedFindAllDTO> toArchivedFindAllDTO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toArchivedFindAllDTO)
      .map(permissionArchivedFindAllCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionArchivedFindAllCo.getName()))
          .ifPresent(permissionArchivedFindAllCo::setName);
        return permissionArchivedFindAllCo;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionArchivedFindAllSliceDTO> toArchivedFindAllSliceDTO(
    Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toArchivedFindAllSliceDTO)
      .map(permissionArchivedFindAllSliceCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionArchivedFindAllSliceCo.getName()))
          .ifPresent(permissionArchivedFindAllSliceCo::setName);
        return permissionArchivedFindAllSliceCo;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<PermissionArchivedPO> toArchivedPO(PermissionPO permissionPO) {
    return Optional.ofNullable(permissionPO).map(PermissionMapper.INSTANCE::toArchivedPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionArchivedPO> toArchivedPO(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toArchivedPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<PermissionPO> toPO(PermissionArchivedPO permissionArchivedPO) {
    return Optional.ofNullable(permissionArchivedPO).map(PermissionMapper.INSTANCE::toPO);
  }

  @Contract("_ -> new")
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

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionFindAllGrpcDTO> toPermissionFindAllGrpcDTO(
    PermissionFindAllDTO permissionFindAllDTO) {
    return Optional.ofNullable(permissionFindAllDTO)
      .map(PermissionMapper.INSTANCE::toPermissionFindAllGrpcDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindByIdGrpcDTO> toPermissionFindByIdGrpcDTO(
    PermissionFindByIdDTO permissionFindByIdDTO) {
    return Optional.ofNullable(permissionFindByIdDTO)
      .map(PermissionMapper.INSTANCE::toPermissionFindByIdGrpcDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindRootDTO> toPermissionFindRootDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionFindRootDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindDirectDTO> toPermissionFindDirectDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionFindDirectDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<PermissionDownloadAllDTO> toPermissionDownloadAllDTO(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionDownloadAllDTO);
  }

  @Contract("_ -> new")
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
              .map(PermissionMapper.INSTANCE::toPermissionIncludePathDTO)
              .collect(Collectors.toList()));
        }
        return permissionIncludePathDownloadAllDTO;
      });
  }
}
