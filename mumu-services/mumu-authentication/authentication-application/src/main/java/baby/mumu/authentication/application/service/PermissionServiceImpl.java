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
package baby.mumu.authentication.application.service;

import baby.mumu.authentication.application.permission.executor.PermissionAddAncestorCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionAddCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionArchiveByIdCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionArchivedFindAllCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionArchivedFindAllSliceCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionDeleteByCodeCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionDeleteByIdCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionDeletePathCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionFindAllCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionFindAllSliceCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionFindByIdCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionFindDirectCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionFindRootCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionRecoverFromArchiveByIdCmdExe;
import baby.mumu.authentication.application.permission.executor.PermissionUpdateCmdExe;
import baby.mumu.authentication.client.api.PermissionService;
import baby.mumu.authentication.client.api.grpc.PageOfPermissionFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfPermissionFindAllGrpcCo.Builder;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PermissionFindByIdGrpcCo;
import baby.mumu.authentication.client.api.grpc.PermissionServiceGrpc.PermissionServiceImplBase;
import baby.mumu.authentication.client.dto.PermissionAddAncestorCmd;
import baby.mumu.authentication.client.dto.PermissionAddCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindDirectCmd;
import baby.mumu.authentication.client.dto.PermissionFindRootCmd;
import baby.mumu.authentication.client.dto.PermissionUpdateCmd;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindByIdCo;
import baby.mumu.authentication.client.dto.co.PermissionFindDirectCo;
import baby.mumu.authentication.client.dto.co.PermissionFindRootCo;
import baby.mumu.authentication.infrastructure.permission.convertor.PermissionConvertor;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 权限管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "PermissionServiceImpl")
public class PermissionServiceImpl extends PermissionServiceImplBase implements PermissionService {

  private final PermissionAddCmdExe permissionAddCmdExe;
  private final PermissionDeleteByIdCmdExe permissionDeleteByIdCmdExe;
  private final PermissionUpdateCmdExe permissionUpdateCmdExe;
  private final PermissionFindAllCmdExe permissionFindAllCmdExe;
  private final PermissionFindByIdCmdExe permissionFindByIdCmdExe;
  private final PermissionArchiveByIdCmdExe permissionArchiveByIdCmdExe;
  private final PermissionRecoverFromArchiveByIdCmdExe permissionRecoverFromArchiveByIdCmdExe;
  private final PermissionArchivedFindAllCmdExe permissionArchivedFindAllCmdExe;
  private final PermissionFindAllSliceCmdExe permissionFindAllSliceCmdExe;
  private final PermissionArchivedFindAllSliceCmdExe permissionArchivedFindAllSliceCmdExe;
  private final PermissionConvertor permissionConvertor;
  private final PermissionAddAncestorCmdExe permissionAddAncestorCmdExe;
  private final PermissionFindRootCmdExe permissionFindRootCmdExe;
  private final PermissionFindDirectCmdExe permissionFindDirectCmdExe;
  private final PermissionDeletePathCmdExe permissionDeletePathCmdExe;
  private final PermissionDeleteByCodeCmdExe permissionDeleteByCodeCmdExe;

  @Autowired
  public PermissionServiceImpl(PermissionAddCmdExe permissionAddCmdExe,
    PermissionDeleteByIdCmdExe permissionDeleteByIdCmdExe,
    PermissionUpdateCmdExe permissionUpdateCmdExe,
    PermissionFindAllCmdExe permissionFindAllCmdExe,
    PermissionFindByIdCmdExe permissionFindByIdCmdExe,
    PermissionArchiveByIdCmdExe permissionArchiveByIdCmdExe,
    PermissionRecoverFromArchiveByIdCmdExe permissionRecoverFromArchiveByIdCmdExe,
    PermissionArchivedFindAllCmdExe permissionArchivedFindAllCmdExe,
    PermissionFindAllSliceCmdExe permissionFindAllSliceCmdExe,
    PermissionArchivedFindAllSliceCmdExe permissionArchivedFindAllSliceCmdExe,
    PermissionConvertor permissionConvertor,
    PermissionAddAncestorCmdExe permissionAddAncestorCmdExe,
    PermissionFindRootCmdExe permissionFindRootCmdExe,
    PermissionFindDirectCmdExe permissionFindDirectCmdExe,
    PermissionDeletePathCmdExe permissionDeletePathCmdExe,
    PermissionDeleteByCodeCmdExe permissionDeleteByCodeCmdExe) {
    this.permissionAddCmdExe = permissionAddCmdExe;
    this.permissionDeleteByIdCmdExe = permissionDeleteByIdCmdExe;
    this.permissionUpdateCmdExe = permissionUpdateCmdExe;
    this.permissionFindAllCmdExe = permissionFindAllCmdExe;
    this.permissionFindByIdCmdExe = permissionFindByIdCmdExe;
    this.permissionArchiveByIdCmdExe = permissionArchiveByIdCmdExe;
    this.permissionRecoverFromArchiveByIdCmdExe = permissionRecoverFromArchiveByIdCmdExe;
    this.permissionArchivedFindAllCmdExe = permissionArchivedFindAllCmdExe;
    this.permissionFindAllSliceCmdExe = permissionFindAllSliceCmdExe;
    this.permissionArchivedFindAllSliceCmdExe = permissionArchivedFindAllSliceCmdExe;
    this.permissionConvertor = permissionConvertor;
    this.permissionAddAncestorCmdExe = permissionAddAncestorCmdExe;
    this.permissionFindRootCmdExe = permissionFindRootCmdExe;
    this.permissionFindDirectCmdExe = permissionFindDirectCmdExe;
    this.permissionDeletePathCmdExe = permissionDeletePathCmdExe;
    this.permissionDeleteByCodeCmdExe = permissionDeleteByCodeCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void add(PermissionAddCmd permissionAddCmd) {
    permissionAddCmdExe.execute(permissionAddCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    permissionDeleteByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByCode(String code) {
    permissionDeleteByCodeCmdExe.execute(code);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateById(PermissionUpdateCmd permissionUpdateCmd) {
    permissionUpdateCmdExe.execute(permissionUpdateCmd);
  }

  @Override
  public Page<PermissionFindAllCo> findAll(
    PermissionFindAllCmd permissionFindAllCmd) {
    return permissionFindAllCmdExe.execute(permissionFindAllCmd);
  }

  @Override
  public Slice<PermissionFindAllSliceCo> findAllSlice(
    PermissionFindAllSliceCmd permissionFindAllSliceCmd) {
    return permissionFindAllSliceCmdExe.execute(permissionFindAllSliceCmd);
  }

  @Override
  public Page<PermissionArchivedFindAllCo> findArchivedAll(
    PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
    return permissionArchivedFindAllCmdExe.execute(permissionArchivedFindAllCmd);
  }

  @Override
  public Slice<PermissionArchivedFindAllSliceCo> findArchivedAllSlice(
    PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
    return permissionArchivedFindAllSliceCmdExe.execute(permissionArchivedFindAllSliceCmd);
  }

  @Override
  public PermissionFindByIdCo findById(Long id) {
    return permissionFindByIdCmdExe.execute(id);
  }

  @Override
  public void findById(Int64Value request,
    StreamObserver<PermissionFindByIdGrpcCo> responseObserver) {
    Runnable runnable = () -> {
      throw new MuMuException(ResponseCode.PERMISSION_DOES_NOT_EXIST);
    };
    Optional.ofNullable(request).filter(Int64Value::isInitialized).ifPresentOrElse(
      (id) -> permissionConvertor.toPermissionFindByIdGrpcCo(
          permissionFindByIdCmdExe.execute(id.getValue()))
        .ifPresentOrElse((permissionFindByIdGrpcCo) -> {
          responseObserver.onNext(permissionFindByIdGrpcCo);
          responseObserver.onCompleted();
        }, runnable), runnable);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void findAll(PermissionFindAllGrpcCmd request,
    StreamObserver<PageOfPermissionFindAllGrpcCo> responseObserver) {
    permissionConvertor.toPermissionFindAllCmd(request)
      .ifPresentOrElse((permissionFindAllCmdNotNull) -> {
        Builder builder = PageOfPermissionFindAllGrpcCo.newBuilder();
        Page<PermissionFindAllCo> permissionFindAllCos = permissionFindAllCmdExe.execute(
          permissionFindAllCmdNotNull);
        List<PermissionFindAllGrpcCo> findAllGrpcCos = permissionFindAllCos.getContent().stream()
          .flatMap(
            permissionFindAllCo -> permissionConvertor.toPermissionFindAllGrpcCo(
                permissionFindAllCo)
              .stream()).toList();
        builder.addAllContent(findAllGrpcCos);
        builder.setTotalPages(permissionFindAllCos.getTotalPages());
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
      }, () -> {
        responseObserver.onNext(PageOfPermissionFindAllGrpcCo.getDefaultInstance());
        responseObserver.onCompleted();
      });

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(Long id) {
    permissionArchiveByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(
    Long id) {
    permissionRecoverFromArchiveByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAncestor(PermissionAddAncestorCmd permissionAddAncestorCmd) {
    permissionAddAncestorCmdExe.execute(permissionAddAncestorCmd);
  }

  @Override
  public Page<PermissionFindRootCo> findRootPermissions(
    PermissionFindRootCmd permissionFindRootCmd) {
    return permissionFindRootCmdExe.execute(permissionFindRootCmd);
  }

  @Override
  public Page<PermissionFindDirectCo> findDirectPermissions(
    PermissionFindDirectCmd permissionFindDirectCmd) {
    return permissionFindDirectCmdExe.execute(permissionFindDirectCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    permissionDeletePathCmdExe.execute(ancestorId, descendantId);
  }
}
