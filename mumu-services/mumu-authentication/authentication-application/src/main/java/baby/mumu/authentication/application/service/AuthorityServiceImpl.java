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

import baby.mumu.authentication.application.authority.executor.AuthorityAddCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityArchiveByIdCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityArchivedFindAllCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityArchivedFindAllSliceCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityDeleteByIdCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityFindAllCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityFindAllSliceCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityFindByIdCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityRecoverFromArchiveByIdCmdExe;
import baby.mumu.authentication.application.authority.executor.AuthorityUpdateCmdExe;
import baby.mumu.authentication.client.api.AuthorityService;
import baby.mumu.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityAddGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityDeleteByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcQueryCo;
import baby.mumu.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceImplBase;
import baby.mumu.authentication.client.api.grpc.AuthorityUpdateGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityUpdateGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo.Builder;
import baby.mumu.authentication.client.dto.AuthorityAddCmd;
import baby.mumu.authentication.client.dto.AuthorityArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityDeleteByIdCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityFindByIdCmd;
import baby.mumu.authentication.client.dto.AuthorityRecoverFromArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AuthorityUpdateCmd;
import baby.mumu.authentication.client.dto.co.AuthorityAddCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllQueryCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindByIdCo;
import baby.mumu.authentication.client.dto.co.AuthorityUpdateCo;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
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
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "AuthorityServiceImpl")
public class AuthorityServiceImpl extends AuthorityServiceImplBase implements AuthorityService {

  private final AuthorityAddCmdExe authorityAddCmdExe;
  private final AuthorityDeleteByIdCmdExe authorityDeleteByIdCmdExe;
  private final AuthorityUpdateCmdExe authorityUpdateCmdExe;
  private final AuthorityFindAllCmdExe authorityFindAllCmdExe;
  private final AuthorityFindByIdCmdExe authorityFindByIdCmdExe;
  private final AuthorityArchiveByIdCmdExe authorityArchiveByIdCmdExe;
  private final AuthorityRecoverFromArchiveByIdCmdExe authorityRecoverFromArchiveByIdCmdExe;
  private final AuthorityArchivedFindAllCmdExe authorityArchivedFindAllCmdExe;
  private final AuthorityFindAllSliceCmdExe authorityFindAllSliceCmdExe;
  private final AuthorityArchivedFindAllSliceCmdExe authorityArchivedFindAllSliceCmdExe;

  @Autowired
  public AuthorityServiceImpl(AuthorityAddCmdExe authorityAddCmdExe,
      AuthorityDeleteByIdCmdExe authorityDeleteByIdCmdExe,
      AuthorityUpdateCmdExe authorityUpdateCmdExe,
      AuthorityFindAllCmdExe authorityFindAllCmdExe,
      AuthorityFindByIdCmdExe authorityFindByIdCmdExe,
      AuthorityArchiveByIdCmdExe authorityArchiveByIdCmdExe,
      AuthorityRecoverFromArchiveByIdCmdExe authorityRecoverFromArchiveByIdCmdExe,
      AuthorityArchivedFindAllCmdExe authorityArchivedFindAllCmdExe,
      AuthorityFindAllSliceCmdExe authorityFindAllSliceCmdExe,
      AuthorityArchivedFindAllSliceCmdExe authorityArchivedFindAllSliceCmdExe) {
    this.authorityAddCmdExe = authorityAddCmdExe;
    this.authorityDeleteByIdCmdExe = authorityDeleteByIdCmdExe;
    this.authorityUpdateCmdExe = authorityUpdateCmdExe;
    this.authorityFindAllCmdExe = authorityFindAllCmdExe;
    this.authorityFindByIdCmdExe = authorityFindByIdCmdExe;
    this.authorityArchiveByIdCmdExe = authorityArchiveByIdCmdExe;
    this.authorityRecoverFromArchiveByIdCmdExe = authorityRecoverFromArchiveByIdCmdExe;
    this.authorityArchivedFindAllCmdExe = authorityArchivedFindAllCmdExe;
    this.authorityFindAllSliceCmdExe = authorityFindAllSliceCmdExe;
    this.authorityArchivedFindAllSliceCmdExe = authorityArchivedFindAllSliceCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void add(AuthorityAddCmd authorityAddCmd) {
    authorityAddCmdExe.execute(authorityAddCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void add(AuthorityAddGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AuthorityAddCmd authorityAddCmd = new AuthorityAddCmd();
    AuthorityAddCo authorityAddCo = getAuthorityAddCo(
        request);
    authorityAddCmd.setAuthorityAddCo(authorityAddCo);
    try {
      authorityAddCmdExe.execute(authorityAddCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @NotNull
  private static AuthorityAddCo getAuthorityAddCo(
      @NotNull AuthorityAddGrpcCmd request) {
    AuthorityAddCo authorityAddCo = new AuthorityAddCo();
    AuthorityAddGrpcCo authorityAddGrpcCo = request.getAuthorityAddCo();
    authorityAddCo.setId(authorityAddGrpcCo.hasId() ? authorityAddGrpcCo.getId().getValue() : null);
    authorityAddCo.setCode(
        authorityAddGrpcCo.hasCode() ? authorityAddGrpcCo.getCode().getValue() : null);
    authorityAddCo.setName(
        authorityAddGrpcCo.hasName() ? authorityAddGrpcCo.getName().getValue() : null);
    return authorityAddCo;
  }

  @NotNull
  private static AuthorityUpdateCo getAuthorityUpdateCo(
      @NotNull AuthorityUpdateGrpcCmd request) {
    AuthorityUpdateCo authorityUpdateCo = new AuthorityUpdateCo();
    AuthorityUpdateGrpcCo authorityUpdateGrpcCo = request.getAuthorityUpdateCo();
    authorityUpdateCo.setId(
        authorityUpdateGrpcCo.hasId() ? authorityUpdateGrpcCo.getId().getValue() : null);
    authorityUpdateCo.setCode(
        authorityUpdateGrpcCo.hasCode() ? authorityUpdateGrpcCo.getCode().getValue() : null);
    authorityUpdateCo.setName(
        authorityUpdateGrpcCo.hasName() ? authorityUpdateGrpcCo.getName().getValue() : null);
    return authorityUpdateCo;
  }

  @NotNull
  private static AuthorityFindAllQueryCo getAuthorityFindAllQueryCo(
      @NotNull AuthorityFindAllGrpcCmd request) {
    AuthorityFindAllQueryCo authorityFindAllQueryCo = new AuthorityFindAllQueryCo();
    AuthorityFindAllGrpcQueryCo authorityFindAllGrpcQueryCo = request.getAuthorityFindAllGrpcQueryCo();
    authorityFindAllQueryCo.setId(
        authorityFindAllGrpcQueryCo.hasId() ? authorityFindAllGrpcQueryCo.getId().getValue()
            : null);
    authorityFindAllQueryCo.setCode(
        authorityFindAllGrpcQueryCo.hasCode() ? authorityFindAllGrpcQueryCo.getCode().getValue()
            : null);
    authorityFindAllQueryCo.setName(
        authorityFindAllGrpcQueryCo.hasName() ? authorityFindAllGrpcQueryCo.getName().getValue()
            : null);
    return authorityFindAllQueryCo;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(AuthorityDeleteByIdCmd authorityDeleteByIdCmd) {
    authorityDeleteByIdCmdExe.execute(authorityDeleteByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateById(AuthorityUpdateCmd authorityUpdateCmd) {
    authorityUpdateCmdExe.execute(authorityUpdateCmd);
  }

  @Override
  public Page<AuthorityFindAllCo> findAll(
      AuthorityFindAllCmd authorityFindAllCmd) {
    return authorityFindAllCmdExe.execute(authorityFindAllCmd);
  }

  @Override
  public Slice<AuthorityFindAllSliceCo> findAllSlice(
      AuthorityFindAllSliceCmd authorityFindAllSliceCmd) {
    return authorityFindAllSliceCmdExe.execute(authorityFindAllSliceCmd);
  }

  @Override
  public Page<AuthorityArchivedFindAllCo> findArchivedAll(
      AuthorityArchivedFindAllCmd authorityArchivedFindAllCmd) {
    return authorityArchivedFindAllCmdExe.execute(authorityArchivedFindAllCmd);
  }

  @Override
  public Slice<AuthorityArchivedFindAllSliceCo> findArchivedAllSlice(
      AuthorityArchivedFindAllSliceCmd authorityArchivedFindAllSliceCmd) {
    return authorityArchivedFindAllSliceCmdExe.execute(authorityArchivedFindAllSliceCmd);
  }

  @Override
  public AuthorityFindByIdCo findById(AuthorityFindByIdCmd authorityFindByIdCmd) {
    return authorityFindByIdCmdExe.execute(authorityFindByIdCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void deleteById(@NotNull AuthorityDeleteByIdGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AuthorityDeleteByIdCmd authorityDeleteByIdCmd = new AuthorityDeleteByIdCmd();
    //noinspection DuplicatedCode
    authorityDeleteByIdCmd.setId(request.hasId() ? request.getId().getValue() : null);
    try {
      authorityDeleteByIdCmdExe.execute(authorityDeleteByIdCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void updateById(AuthorityUpdateGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AuthorityUpdateCmd authorityUpdateCmd = new AuthorityUpdateCmd();
    AuthorityUpdateCo authorityUpdateCo = getAuthorityUpdateCo(
        request);
    authorityUpdateCmd.setAuthorityUpdateCo(authorityUpdateCo);
    try {
      authorityUpdateCmdExe.execute(authorityUpdateCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void findAll(AuthorityFindAllGrpcCmd request,
      StreamObserver<PageOfAuthorityFindAllGrpcCo> responseObserver) {
    AuthorityFindAllCmd authorityFindAllCmd = new AuthorityFindAllCmd();
    authorityFindAllCmd.setAuthorityFindAllQueryCo(getAuthorityFindAllQueryCo(request));
    authorityFindAllCmd.setCurrent(request.hasPageNo() ? request.getPageNo().getValue() : 0);
    authorityFindAllCmd.setPageSize(request.hasPageSize() ? request.getPageSize().getValue() : 10);
    Builder builder = PageOfAuthorityFindAllGrpcCo.newBuilder();
    try {
      Page<AuthorityFindAllCo> authorityFindAllCos = authorityFindAllCmdExe.execute(
          authorityFindAllCmd);
      List<AuthorityFindAllGrpcCo> findAllGrpcCos = authorityFindAllCos.getContent().stream()
          .map(authorityFindAllCo -> AuthorityFindAllGrpcCo.newBuilder()
              .setId(Int64Value.of(authorityFindAllCo.getId()))
              .setCode(StringValue.of(authorityFindAllCo.getCode())).setName(
                  StringValue.of(authorityFindAllCo.getName())).build()).toList();
      builder.addAllContent(findAllGrpcCos);
      builder.setTotalPages(authorityFindAllCos.getTotalPages());
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(AuthorityArchiveByIdCmd authorityArchiveByIdCmd) {
    authorityArchiveByIdCmdExe.execute(authorityArchiveByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(
      AuthorityRecoverFromArchiveByIdCmd authorityRecoverFromArchiveByIdCmd) {
    authorityRecoverFromArchiveByIdCmdExe.execute(authorityRecoverFromArchiveByIdCmd);
  }
}
