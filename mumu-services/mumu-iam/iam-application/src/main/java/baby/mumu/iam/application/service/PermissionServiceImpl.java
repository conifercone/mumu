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

package baby.mumu.iam.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.iam.application.permission.executor.PermissionAddAncestorCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionAddCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionArchiveByIdCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionArchivedFindAllCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionArchivedFindAllSliceCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionDeleteByCodeCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionDeleteByIdCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionDeletePathCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionDownloadAllCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionFindAllCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionFindAllSliceCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionFindByCodeCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionFindByIdCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionFindDirectCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionFindRootCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionIncludePathDownloadAllCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionRecoverFromArchiveByIdCmdExe;
import baby.mumu.iam.application.permission.executor.PermissionUpdateCmdExe;
import baby.mumu.iam.client.api.PermissionService;
import baby.mumu.iam.client.api.grpc.PageOfPermissionFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.PageOfPermissionFindAllGrpcDTO.Builder;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionServiceGrpc.PermissionServiceImplBase;
import baby.mumu.iam.client.cmds.PermissionAddAncestorCmd;
import baby.mumu.iam.client.cmds.PermissionAddCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionFindDirectCmd;
import baby.mumu.iam.client.cmds.PermissionFindRootCmd;
import baby.mumu.iam.client.cmds.PermissionUpdateCmd;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllDTO;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionFindAllDTO;
import baby.mumu.iam.client.dto.PermissionFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionFindByCodeDTO;
import baby.mumu.iam.client.dto.PermissionFindByIdDTO;
import baby.mumu.iam.client.dto.PermissionFindDirectDTO;
import baby.mumu.iam.client.dto.PermissionFindRootDTO;
import baby.mumu.iam.infrastructure.permission.convertor.PermissionConvertor;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletResponse;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
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
  private final PermissionDownloadAllCmdExe permissionDownloadAllCmdExe;
  private final PermissionFindByCodeCmdExe permissionFindByCodeCmdExe;
  private final PermissionIncludePathDownloadAllCmdExe permissionIncludePathDownloadAllCmdExe;

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
    PermissionDeleteByCodeCmdExe permissionDeleteByCodeCmdExe,
    PermissionDownloadAllCmdExe permissionDownloadAllCmdExe,
    PermissionFindByCodeCmdExe permissionFindByCodeCmdExe,
    PermissionIncludePathDownloadAllCmdExe permissionIncludePathDownloadAllCmdExe) {
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
    this.permissionDownloadAllCmdExe = permissionDownloadAllCmdExe;
    this.permissionFindByCodeCmdExe = permissionFindByCodeCmdExe;
    this.permissionIncludePathDownloadAllCmdExe = permissionIncludePathDownloadAllCmdExe;
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
  public Page<PermissionFindAllDTO> findAll(
    PermissionFindAllCmd permissionFindAllCmd) {
    return permissionFindAllCmdExe.execute(permissionFindAllCmd);
  }

  @Override
  public Slice<PermissionFindAllSliceDTO> findAllSlice(
    PermissionFindAllSliceCmd permissionFindAllSliceCmd) {
    return permissionFindAllSliceCmdExe.execute(permissionFindAllSliceCmd);
  }

  @Override
  public Page<PermissionArchivedFindAllDTO> findArchivedAll(
    PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
    return permissionArchivedFindAllCmdExe.execute(permissionArchivedFindAllCmd);
  }

  @Override
  public Slice<PermissionArchivedFindAllSliceDTO> findArchivedAllSlice(
    PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
    return permissionArchivedFindAllSliceCmdExe.execute(permissionArchivedFindAllSliceCmd);
  }

  @Override
  public PermissionFindByIdDTO findById(Long id) {
    return permissionFindByIdCmdExe.execute(id);
  }

  @Override
  public PermissionFindByCodeDTO findByCode(String code) {
    return permissionFindByCodeCmdExe.execute(code);
  }

  @Override
  public void findById(Int64Value request,
    StreamObserver<PermissionFindByIdGrpcDTO> responseObserver) {
    Runnable runnable = () -> {
      throw new MuMuException(ResponseCode.PERMISSION_DOES_NOT_EXIST);
    };
    Optional.ofNullable(request).filter(Int64Value::isInitialized).ifPresentOrElse(
      (id) -> permissionConvertor.toPermissionFindByIdGrpcDTO(
          permissionFindByIdCmdExe.execute(id.getValue()))
        .ifPresentOrElse((permissionFindByIdGrpcDTO) -> {
          responseObserver.onNext(permissionFindByIdGrpcDTO);
          responseObserver.onCompleted();
        }, runnable), runnable);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void findAll(PermissionFindAllGrpcCmd request,
    StreamObserver<PageOfPermissionFindAllGrpcDTO> responseObserver) {
    permissionConvertor.toPermissionFindAllCmd(request)
      .ifPresentOrElse((permissionFindAllCmdNotNull) -> {
        Builder builder = PageOfPermissionFindAllGrpcDTO.newBuilder();
        Page<PermissionFindAllDTO> permissionFindAllCos = permissionFindAllCmdExe.execute(
          permissionFindAllCmdNotNull);
        List<PermissionFindAllGrpcDTO> findAllGrpcCos = permissionFindAllCos.getContent().stream()
          .flatMap(
            permissionFindAllCo -> permissionConvertor.toPermissionFindAllGrpcDTO(
                permissionFindAllCo)
              .stream()).toList();
        builder.addAllContent(findAllGrpcCos);
        builder.setTotalPages(permissionFindAllCos.getTotalPages());
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
      }, () -> {
        responseObserver.onNext(PageOfPermissionFindAllGrpcDTO.getDefaultInstance());
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
  public Page<PermissionFindRootDTO> findRootPermissions(
    PermissionFindRootCmd permissionFindRootCmd) {
    return permissionFindRootCmdExe.execute(permissionFindRootCmd);
  }

  @Override
  public Page<PermissionFindDirectDTO> findDirectPermissions(
    PermissionFindDirectCmd permissionFindDirectCmd) {
    return permissionFindDirectCmdExe.execute(permissionFindDirectCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    permissionDeletePathCmdExe.execute(ancestorId, descendantId);
  }

  @Override
  @Transactional(readOnly = true)
  public void downloadAll(HttpServletResponse response) {
    permissionDownloadAllCmdExe.execute(response);
  }

  @Override
  @Transactional(readOnly = true)
  public void downloadAllIncludePath(HttpServletResponse response) {
    permissionIncludePathDownloadAllCmdExe.execute(response);
  }
}
