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

import baby.mumu.authentication.application.role.executor.RoleAddAncestorCmdExe;
import baby.mumu.authentication.application.role.executor.RoleAddCmdExe;
import baby.mumu.authentication.application.role.executor.RoleArchiveByIdCmdExe;
import baby.mumu.authentication.application.role.executor.RoleArchivedFindAllCmdExe;
import baby.mumu.authentication.application.role.executor.RoleArchivedFindAllSliceCmdExe;
import baby.mumu.authentication.application.role.executor.RoleDeleteByIdCmdExe;
import baby.mumu.authentication.application.role.executor.RoleDeletePathCmdExe;
import baby.mumu.authentication.application.role.executor.RoleFindAllCmdExe;
import baby.mumu.authentication.application.role.executor.RoleFindAllSliceCmdExe;
import baby.mumu.authentication.application.role.executor.RoleFindByIdCmdExe;
import baby.mumu.authentication.application.role.executor.RoleFindDirectCmdExe;
import baby.mumu.authentication.application.role.executor.RoleFindRootCmdExe;
import baby.mumu.authentication.application.role.executor.RoleRecoverFromArchiveByIdCmdExe;
import baby.mumu.authentication.application.role.executor.RoleUpdateCmdExe;
import baby.mumu.authentication.client.api.RoleService;
import baby.mumu.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo.Builder;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.RoleFindByIdGrpcCo;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceImplBase;
import baby.mumu.authentication.client.dto.RoleAddAncestorCmd;
import baby.mumu.authentication.client.dto.RoleAddCmd;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.RoleFindAllCmd;
import baby.mumu.authentication.client.dto.RoleFindAllSliceCmd;
import baby.mumu.authentication.client.dto.RoleFindDirectCmd;
import baby.mumu.authentication.client.dto.RoleFindRootCmd;
import baby.mumu.authentication.client.dto.RoleUpdateCmd;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleFindByIdCo;
import baby.mumu.authentication.client.dto.co.RoleFindDirectCo;
import baby.mumu.authentication.client.dto.co.RoleFindRootCo;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
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
 * 角色管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "RoleServiceImpl")
public class RoleServiceImpl extends RoleServiceImplBase implements RoleService {

  private final RoleAddCmdExe roleAddCmdExe;
  private final RoleDeleteByIdCmdExe roleDeleteByIdCmdExe;
  private final RoleUpdateCmdExe roleUpdateCmdExe;
  private final RoleFindAllCmdExe roleFindAllCmdExe;
  private final RoleArchiveByIdCmdExe roleArchiveByIdCmdExe;
  private final RoleRecoverFromArchiveByIdCmdExe roleRecoverFromArchiveByIdCmdExe;
  private final RoleFindAllSliceCmdExe roleFindAllSliceCmdExe;
  private final RoleArchivedFindAllCmdExe roleArchivedFindAllCmdExe;
  private final RoleArchivedFindAllSliceCmdExe roleArchivedFindAllSliceCmdExe;
  private final RoleConvertor roleConvertor;
  private final RoleFindByIdCmdExe roleFindByIdCmdExe;
  private final RoleAddAncestorCmdExe roleAddAncestorCmdExe;
  private final RoleFindRootCmdExe roleFindRootCmdExe;
  private final RoleFindDirectCmdExe roleFindDirectCmdExe;
  private final RoleDeletePathCmdExe roleDeletePathCmdExe;

  @Autowired
  public RoleServiceImpl(RoleAddCmdExe roleAddCmdExe, RoleDeleteByIdCmdExe roleDeleteByIdCmdExe,
    RoleUpdateCmdExe roleUpdateCmdExe, RoleFindAllCmdExe roleFindAllCmdExe,
    RoleArchiveByIdCmdExe roleArchiveByIdCmdExe,
    RoleRecoverFromArchiveByIdCmdExe roleRecoverFromArchiveByIdCmdExe,
    RoleFindAllSliceCmdExe roleFindAllSliceCmdExe,
    RoleArchivedFindAllCmdExe roleArchivedFindAllCmdExe,
    RoleArchivedFindAllSliceCmdExe roleArchivedFindAllSliceCmdExe, RoleConvertor roleConvertor,
    RoleFindByIdCmdExe roleFindByIdCmdExe, RoleAddAncestorCmdExe roleAddAncestorCmdExe,
    RoleFindRootCmdExe roleFindRootCmdExe, RoleFindDirectCmdExe roleFindDirectCmdExe,
    RoleDeletePathCmdExe roleDeletePathCmdExe) {
    this.roleAddCmdExe = roleAddCmdExe;
    this.roleDeleteByIdCmdExe = roleDeleteByIdCmdExe;
    this.roleUpdateCmdExe = roleUpdateCmdExe;
    this.roleFindAllCmdExe = roleFindAllCmdExe;
    this.roleArchiveByIdCmdExe = roleArchiveByIdCmdExe;
    this.roleRecoverFromArchiveByIdCmdExe = roleRecoverFromArchiveByIdCmdExe;
    this.roleFindAllSliceCmdExe = roleFindAllSliceCmdExe;
    this.roleArchivedFindAllCmdExe = roleArchivedFindAllCmdExe;
    this.roleArchivedFindAllSliceCmdExe = roleArchivedFindAllSliceCmdExe;
    this.roleConvertor = roleConvertor;
    this.roleFindByIdCmdExe = roleFindByIdCmdExe;
    this.roleAddAncestorCmdExe = roleAddAncestorCmdExe;
    this.roleFindRootCmdExe = roleFindRootCmdExe;
    this.roleFindDirectCmdExe = roleFindDirectCmdExe;
    this.roleDeletePathCmdExe = roleDeletePathCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void add(RoleAddCmd roleAddCmd) {
    roleAddCmdExe.execute(roleAddCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    roleDeleteByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateById(RoleUpdateCmd roleUpdateCmd) {
    roleUpdateCmdExe.execute(roleUpdateCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Page<RoleFindAllCo> findAll(RoleFindAllCmd roleFindAllCmd) {
    return roleFindAllCmdExe.execute(roleFindAllCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Slice<RoleFindAllSliceCo> findAllSlice(RoleFindAllSliceCmd roleFindAllSliceCmd) {
    return roleFindAllSliceCmdExe.execute(roleFindAllSliceCmd);
  }

  @Override
  public Page<RoleArchivedFindAllCo> findArchivedAll(
    RoleArchivedFindAllCmd roleArchivedFindAllCmd) {
    return roleArchivedFindAllCmdExe.execute(roleArchivedFindAllCmd);
  }

  @Override
  public Slice<RoleArchivedFindAllSliceCo> findArchivedAllSlice(
    RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd) {
    return roleArchivedFindAllSliceCmdExe.execute(roleArchivedFindAllSliceCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void findAll(RoleFindAllGrpcCmd request,
    StreamObserver<PageOfRoleFindAllGrpcCo> responseObserver) {
    roleConvertor.toRoleFindAllCmd(request).ifPresentOrElse(roleFindAllCmdNotNull -> {
      Builder builder = PageOfRoleFindAllGrpcCo.newBuilder();
      Page<RoleFindAllCo> roleFindAllCos = roleFindAllCmdExe.execute(
        roleFindAllCmdNotNull);
      List<RoleFindAllGrpcCo> findAllGrpcCos = roleFindAllCos.getContent().stream()
        .flatMap(roleFindAllCo -> roleConvertor.toRoleFindAllGrpcCo(roleFindAllCo).stream())
        .toList();
      builder.addAllContent(findAllGrpcCos);
      builder.setTotalPages(roleFindAllCos.getTotalPages());
      responseObserver.onNext(builder.build());
      responseObserver.onCompleted();
    }, () -> {
      responseObserver.onNext(PageOfRoleFindAllGrpcCo.getDefaultInstance());
      responseObserver.onCompleted();
    });
  }

  @Override
  public void findById(Int64Value request, StreamObserver<RoleFindByIdGrpcCo> responseObserver) {
    Optional.ofNullable(request).filter(Int64Value::isInitialized).map(Int64Value::getValue)
      .map(
        roleFindByIdCmdExe::execute).flatMap(roleConvertor::toRoleFindByIdGrpcCo)
      .ifPresentOrElse((roleFindByIdGrpcCo) -> {
        responseObserver.onNext(roleFindByIdGrpcCo);
        responseObserver.onCompleted();
      }, () -> {
        throw new MuMuException(ResponseCode.ROLE_DOES_NOT_EXIST);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(Long id) {
    roleArchiveByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    roleRecoverFromArchiveByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAncestor(RoleAddAncestorCmd roleAddAncestorCmd) {
    roleAddAncestorCmdExe.execute(roleAddAncestorCmd);
  }

  @Override
  public Page<RoleFindRootCo> findRootRoles(RoleFindRootCmd roleFindRootCmd) {
    return roleFindRootCmdExe.execute(roleFindRootCmd);
  }

  @Override
  public Page<RoleFindDirectCo> findDirectRoles(RoleFindDirectCmd roleFindDirectCmd) {
    return roleFindDirectCmdExe.execute(roleFindDirectCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    roleDeletePathCmdExe.execute(ancestorId, descendantId);
  }

  @Override
  public RoleFindByIdCo findById(Long id) {
    return roleFindByIdCmdExe.execute(id);
  }
}
