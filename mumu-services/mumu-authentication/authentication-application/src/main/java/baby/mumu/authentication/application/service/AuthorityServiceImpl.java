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
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityFindByIdGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceImplBase;
import baby.mumu.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo.Builder;
import baby.mumu.authentication.client.dto.AuthorityAddCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityUpdateCmd;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindByIdCo;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
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
  private final AuthorityConvertor authorityConvertor;

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
    AuthorityArchivedFindAllSliceCmdExe authorityArchivedFindAllSliceCmdExe,
    AuthorityConvertor authorityConvertor) {
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
    this.authorityConvertor = authorityConvertor;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void add(AuthorityAddCmd authorityAddCmd) {
    authorityAddCmdExe.execute(authorityAddCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    authorityDeleteByIdCmdExe.execute(id);
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
  public AuthorityFindByIdCo findById(Long id) {
    return authorityFindByIdCmdExe.execute(id);
  }

  @Override
  public void findById(Int64Value request,
    StreamObserver<AuthorityFindByIdGrpcCo> responseObserver) {
    Runnable runnable = () -> {
      throw new GRpcRuntimeExceptionWrapper(
        new MuMuException(ResponseCode.AUTHORITY_DOES_NOT_EXIST));
    };
    Optional.ofNullable(request).filter(Int64Value::isInitialized).ifPresentOrElse(
      (id) -> authorityConvertor.toAuthorityFindByIdGrpcCo(
          authorityFindByIdCmdExe.execute(id.getValue()))
        .ifPresentOrElse((authorityFindByIdGrpcCo) -> {
          responseObserver.onNext(authorityFindByIdGrpcCo);
          responseObserver.onCompleted();
        }, runnable), runnable);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void findAll(AuthorityFindAllGrpcCmd request,
    StreamObserver<PageOfAuthorityFindAllGrpcCo> responseObserver) {
    authorityConvertor.toAuthorityFindAllCmd(request)
      .ifPresentOrElse((authorityFindAllCmdNotNull) -> {
        Builder builder = PageOfAuthorityFindAllGrpcCo.newBuilder();
        try {
          Page<AuthorityFindAllCo> authorityFindAllCos = authorityFindAllCmdExe.execute(
            authorityFindAllCmdNotNull);
          List<AuthorityFindAllGrpcCo> findAllGrpcCos = authorityFindAllCos.getContent().stream()
            .flatMap(
              authorityFindAllCo -> authorityConvertor.toAuthorityFindAllGrpcCo(authorityFindAllCo)
                .stream()).toList();
          builder.addAllContent(findAllGrpcCos);
          builder.setTotalPages(authorityFindAllCos.getTotalPages());
          responseObserver.onNext(builder.build());
          responseObserver.onCompleted();
        } catch (Exception e) {
          throw new GRpcRuntimeExceptionWrapper(e);
        }
      }, () -> {
        responseObserver.onNext(PageOfAuthorityFindAllGrpcCo.getDefaultInstance());
        responseObserver.onCompleted();
      });

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(Long id) {
    authorityArchiveByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(
    Long id) {
    authorityRecoverFromArchiveByIdCmdExe.execute(id);
  }
}
