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
package baby.mumu.authentication.infrastructure.permission.convertor;

import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PermissionFindByIdGrpcCo;
import baby.mumu.authentication.client.dto.PermissionAddCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionUpdateCmd;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionDownloadAllCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindByIdCo;
import baby.mumu.authentication.client.dto.co.PermissionFindDirectCo;
import baby.mumu.authentication.client.dto.co.PermissionFindRootCo;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionArchivedRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.dataobject.PermissionArchivedDo;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.dataobject.PermissionDo;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.redis.dataobject.PermissionRedisDo;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathsRepository;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import jakarta.validation.Valid;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 权限信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class PermissionConvertor {

  private final PermissionRepository permissionRepository;
  private final SimpleTextTranslation simpleTextTranslation;
  private final PermissionArchivedRepository permissionArchivedRepository;
  private final PermissionPathsRepository permissionPathsRepository;

  @Autowired
  public PermissionConvertor(PermissionRepository permissionRepository,
    ObjectProvider<SimpleTextTranslation> simpleTextTranslation,
    PermissionArchivedRepository permissionArchivedRepository,
    PermissionPathsRepository permissionPathsRepository) {
    this.permissionRepository = permissionRepository;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    this.permissionArchivedRepository = permissionArchivedRepository;
    this.permissionPathsRepository = permissionPathsRepository;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Permission> toEntity(PermissionDo permissionDo) {
    return Optional.ofNullable(permissionDo).map(
      PermissionMapper.INSTANCE::toEntity).flatMap(this::hasDescendant);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<Permission> toEntityDoNotJudgeHasDescendant(PermissionDo permissionDo) {
    return Optional.ofNullable(permissionDo).map(
      PermissionMapper.INSTANCE::toEntity);
  }

  private Optional<Permission> hasDescendant(Permission permission) {
    return Optional.ofNullable(permission).map(permissionNotNull -> {
      permissionNotNull.setHasDescendant(
        permissionPathsRepository.existsDescendantPermissions(permission.getId()));
      return permissionNotNull;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<PermissionDo> toDataObject(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toDataObject);
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
  public Optional<Permission> toEntity(PermissionArchivedDo permissionArchivedDo) {
    return Optional.ofNullable(permissionArchivedDo).map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Permission> toEntity(PermissionRedisDo permissionRedisDo) {
    return Optional.ofNullable(permissionRedisDo).map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionRedisDo> toPermissionRedisDo(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toPermissionRedisDo);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionDo> toDataObject(PermissionRedisDo permissionRedisDo) {
    return Optional.ofNullable(permissionRedisDo).map(PermissionMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<Permission> toEntity(
    @Valid PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
    return Optional.ofNullable(permissionArchivedFindAllCmd)
      .map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Permission> toEntity(
    @Valid PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
    return Optional.ofNullable(permissionArchivedFindAllSliceCmd)
      .map(PermissionMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<PermissionFindByIdCo> toFindByIdCo(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toFindByIdCo)
      .map(permissionFindByIdCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionFindByIdCo.getName()))
          .ifPresent(permissionFindByIdCo::setName);
        return permissionFindByIdCo;
      });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<PermissionFindAllCo> toFindAllCo(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toFindAllCo)
      .map(permissionFindAllCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionFindAllCo.getName()))
          .ifPresent(permissionFindAllCo::setName);
        return permissionFindAllCo;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionFindAllSliceCo> toFindAllSliceCo(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toFindAllSliceCo)
      .map(permissionFindAllSliceCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionFindAllSliceCo.getName()))
          .ifPresent(permissionFindAllSliceCo::setName);
        return permissionFindAllSliceCo;
      });
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<PermissionArchivedFindAllCo> toArchivedFindAllCo(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toArchivedFindAllCo)
      .map(permissionArchivedFindAllCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              permissionArchivedFindAllCo.getName()))
          .ifPresent(permissionArchivedFindAllCo::setName);
        return permissionArchivedFindAllCo;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionArchivedFindAllSliceCo> toArchivedFindAllSliceCo(
    Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toArchivedFindAllSliceCo)
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
  public Optional<PermissionArchivedDo> toArchivedDo(PermissionDo permissionDo) {
    return Optional.ofNullable(permissionDo).map(PermissionMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<PermissionArchivedDo> toArchivedDo(Permission permission) {
    return Optional.ofNullable(permission).map(PermissionMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<PermissionDo> toDataObject(PermissionArchivedDo permissionArchivedDo) {
    return Optional.ofNullable(permissionArchivedDo).map(PermissionMapper.INSTANCE::toDataObject);
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
  public Optional<PermissionFindAllGrpcCo> toPermissionFindAllGrpcCo(
    PermissionFindAllCo permissionFindAllCo) {
    return Optional.ofNullable(permissionFindAllCo)
      .map(PermissionMapper.INSTANCE::toPermissionFindAllGrpcCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindByIdGrpcCo> toPermissionFindByIdGrpcCo(
    PermissionFindByIdCo permissionFindByIdCo) {
    return Optional.ofNullable(permissionFindByIdCo)
      .map(PermissionMapper.INSTANCE::toPermissionFindByIdGrpcCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindRootCo> toPermissionFindRootCo(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionFindRootCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.3.0")
  public Optional<PermissionFindDirectCo> toPermissionFindDirectCo(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionFindDirectCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<PermissionDownloadAllCo> toPermissionDownloadAllCo(
    Permission permission) {
    return Optional.ofNullable(permission)
      .map(PermissionMapper.INSTANCE::toPermissionDownloadAllCo);
  }
}
