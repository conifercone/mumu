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
import baby.mumu.iam.application.role.executor.RoleAddCmdExe;
import baby.mumu.iam.application.role.executor.RoleAddDescendantCmdExe;
import baby.mumu.iam.application.role.executor.RoleArchiveByIdCmdExe;
import baby.mumu.iam.application.role.executor.RoleArchivedFindAllCmdExe;
import baby.mumu.iam.application.role.executor.RoleArchivedFindAllSliceCmdExe;
import baby.mumu.iam.application.role.executor.RoleDeleteByCodeCmdExe;
import baby.mumu.iam.application.role.executor.RoleDeleteByIdCmdExe;
import baby.mumu.iam.application.role.executor.RoleDeletePathCmdExe;
import baby.mumu.iam.application.role.executor.RoleFindAllCmdExe;
import baby.mumu.iam.application.role.executor.RoleFindAllSliceCmdExe;
import baby.mumu.iam.application.role.executor.RoleFindByCodeCmdExe;
import baby.mumu.iam.application.role.executor.RoleFindByIdCmdExe;
import baby.mumu.iam.application.role.executor.RoleFindDirectCmdExe;
import baby.mumu.iam.application.role.executor.RoleFindRootCmdExe;
import baby.mumu.iam.application.role.executor.RoleRecoverFromArchiveByIdCmdExe;
import baby.mumu.iam.application.role.executor.RoleUpdateCmdExe;
import baby.mumu.iam.client.api.RoleService;
import baby.mumu.iam.client.api.grpc.PageOfRoleFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.PageOfRoleFindAllGrpcDTO.Builder;
import baby.mumu.iam.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.RoleFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleServiceGrpc.RoleServiceImplBase;
import baby.mumu.iam.client.cmds.RoleAddCmd;
import baby.mumu.iam.client.cmds.RoleAddDescendantCmd;
import baby.mumu.iam.client.cmds.RoleArchivedFindAllCmd;
import baby.mumu.iam.client.cmds.RoleArchivedFindAllSliceCmd;
import baby.mumu.iam.client.cmds.RoleFindAllCmd;
import baby.mumu.iam.client.cmds.RoleFindAllSliceCmd;
import baby.mumu.iam.client.cmds.RoleFindDirectCmd;
import baby.mumu.iam.client.cmds.RoleFindRootCmd;
import baby.mumu.iam.client.cmds.RoleUpdateCmd;
import baby.mumu.iam.client.dto.RoleArchivedFindAllDTO;
import baby.mumu.iam.client.dto.RoleArchivedFindAllSliceDTO;
import baby.mumu.iam.client.dto.RoleFindAllDTO;
import baby.mumu.iam.client.dto.RoleFindAllSliceDTO;
import baby.mumu.iam.client.dto.RoleFindByCodeDTO;
import baby.mumu.iam.client.dto.RoleFindByIdDTO;
import baby.mumu.iam.client.dto.RoleFindDirectDTO;
import baby.mumu.iam.client.dto.RoleFindRootDTO;
import baby.mumu.iam.client.dto.RoleUpdatedDataDTO;
import baby.mumu.iam.infra.role.convertor.RoleConvertor;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
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
  private final RoleAddDescendantCmdExe roleAddDescendantCmdExe;
  private final RoleFindRootCmdExe roleFindRootCmdExe;
  private final RoleFindDirectCmdExe roleFindDirectCmdExe;
  private final RoleDeletePathCmdExe roleDeletePathCmdExe;
  private final RoleDeleteByCodeCmdExe roleDeleteByCodeCmdExe;
  private final RoleFindByCodeCmdExe roleFindByCodeCmdExe;

  @Autowired
  public RoleServiceImpl(RoleAddCmdExe roleAddCmdExe, RoleDeleteByIdCmdExe roleDeleteByIdCmdExe,
    RoleUpdateCmdExe roleUpdateCmdExe, RoleFindAllCmdExe roleFindAllCmdExe,
    RoleArchiveByIdCmdExe roleArchiveByIdCmdExe,
    RoleRecoverFromArchiveByIdCmdExe roleRecoverFromArchiveByIdCmdExe,
    RoleFindAllSliceCmdExe roleFindAllSliceCmdExe,
    RoleArchivedFindAllCmdExe roleArchivedFindAllCmdExe,
    RoleArchivedFindAllSliceCmdExe roleArchivedFindAllSliceCmdExe, RoleConvertor roleConvertor,
    RoleFindByIdCmdExe roleFindByIdCmdExe, RoleAddDescendantCmdExe roleAddDescendantCmdExe,
    RoleFindRootCmdExe roleFindRootCmdExe, RoleFindDirectCmdExe roleFindDirectCmdExe,
    RoleDeletePathCmdExe roleDeletePathCmdExe, RoleDeleteByCodeCmdExe roleDeleteByCodeCmdExe,
    RoleFindByCodeCmdExe roleFindByCodeCmdExe) {
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
    this.roleAddDescendantCmdExe = roleAddDescendantCmdExe;
    this.roleFindRootCmdExe = roleFindRootCmdExe;
    this.roleFindDirectCmdExe = roleFindDirectCmdExe;
    this.roleDeletePathCmdExe = roleDeletePathCmdExe;
    this.roleDeleteByCodeCmdExe = roleDeleteByCodeCmdExe;
    this.roleFindByCodeCmdExe = roleFindByCodeCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long add(RoleAddCmd roleAddCmd) {
    return roleAddCmdExe.execute(roleAddCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    roleDeleteByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByCode(String code) {
    roleDeleteByCodeCmdExe.execute(code);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RoleUpdatedDataDTO updateById(RoleUpdateCmd roleUpdateCmd) {
    return roleUpdateCmdExe.execute(roleUpdateCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Page<RoleFindAllDTO> findAll(RoleFindAllCmd roleFindAllCmd) {
    return roleFindAllCmdExe.execute(roleFindAllCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Slice<RoleFindAllSliceDTO> findAllSlice(RoleFindAllSliceCmd roleFindAllSliceCmd) {
    return roleFindAllSliceCmdExe.execute(roleFindAllSliceCmd);
  }

  @Override
  public Page<RoleArchivedFindAllDTO> findArchivedAll(
    RoleArchivedFindAllCmd roleArchivedFindAllCmd) {
    return roleArchivedFindAllCmdExe.execute(roleArchivedFindAllCmd);
  }

  @Override
  public Slice<RoleArchivedFindAllSliceDTO> findArchivedAllSlice(
    RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd) {
    return roleArchivedFindAllSliceCmdExe.execute(roleArchivedFindAllSliceCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void findAll(RoleFindAllGrpcCmd request,
    StreamObserver<PageOfRoleFindAllGrpcDTO> responseObserver) {
    roleConvertor.toRoleFindAllCmd(request).ifPresentOrElse(roleFindAllCmdNotNull -> {
      Builder builder = PageOfRoleFindAllGrpcDTO.newBuilder();
      Page<RoleFindAllDTO> roleFindAllCos = roleFindAllCmdExe.execute(
        roleFindAllCmdNotNull);
      List<RoleGrpcDTO> findAllGrpcCos = roleFindAllCos.getContent().stream()
        .flatMap(roleFindAllCo -> roleConvertor.toRoleGrpcDTO(roleFindAllCo).stream())
        .toList();
      builder.addAllContent(findAllGrpcCos);
      builder.setTotalPages(roleFindAllCos.getTotalPages());
      responseObserver.onNext(builder.build());
      responseObserver.onCompleted();
    }, () -> {
      responseObserver.onNext(PageOfRoleFindAllGrpcDTO.getDefaultInstance());
      responseObserver.onCompleted();
    });
  }

  @Override
  public void findById(Int64Value request, StreamObserver<RoleFindByIdGrpcDTO> responseObserver) {
    Optional.ofNullable(request).filter(Int64Value::isInitialized).map(Int64Value::getValue)
      .map(
        roleFindByIdCmdExe::execute).flatMap(roleConvertor::toRoleFindByIdGrpcDTO)
      .ifPresentOrElse((roleFindByIdGrpcDTO) -> {
        responseObserver.onNext(roleFindByIdGrpcDTO);
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
  public void addDescendant(RoleAddDescendantCmd roleAddDescendantCmd) {
    roleAddDescendantCmdExe.execute(roleAddDescendantCmd);
  }

  @Override
  public Page<RoleFindRootDTO> findRootRoles(RoleFindRootCmd roleFindRootCmd) {
    return roleFindRootCmdExe.execute(roleFindRootCmd);
  }

  @Override
  public Page<RoleFindDirectDTO> findDirectRoles(RoleFindDirectCmd roleFindDirectCmd) {
    return roleFindDirectCmdExe.execute(roleFindDirectCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    roleDeletePathCmdExe.execute(ancestorId, descendantId);
  }

  @Override
  public RoleFindByIdDTO findById(Long id) {
    return roleFindByIdCmdExe.execute(id);
  }

  @Override
  public RoleFindByCodeDTO findByCode(String code) {
    return roleFindByCodeCmdExe.execute(code);
  }
}
