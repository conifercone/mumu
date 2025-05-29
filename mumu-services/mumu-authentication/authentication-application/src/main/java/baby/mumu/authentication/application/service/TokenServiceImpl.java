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
package baby.mumu.authentication.application.service;

import baby.mumu.authentication.application.token.executor.TokenValidityCmdExe;
import baby.mumu.authentication.client.api.TokenService;
import baby.mumu.authentication.client.api.grpc.TokenServiceGrpc.TokenServiceImplBase;
import baby.mumu.authentication.client.api.grpc.TokenValidityGrpcCmd;
import baby.mumu.authentication.client.api.grpc.TokenValidityGrpcDTO;
import baby.mumu.authentication.client.cmds.TokenValidityCmd;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * token service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "TokenServiceImpl")
public class TokenServiceImpl extends TokenServiceImplBase implements TokenService {


  private final TokenValidityCmdExe tokenValidityCmdExe;

  @Autowired
  public TokenServiceImpl(TokenValidityCmdExe tokenValidityCmdExe) {
    this.tokenValidityCmdExe = tokenValidityCmdExe;
  }

  @Override
  public boolean validity(TokenValidityCmd tokenValidityCmd) {
    return tokenValidityCmdExe.execute(tokenValidityCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void validity(@NotNull TokenValidityGrpcCmd request,
    @NotNull StreamObserver<TokenValidityGrpcDTO> responseObserver) {
    TokenValidityCmd tokenValidityCmd = new TokenValidityCmd();
    tokenValidityCmd.setToken(request.getToken());
    responseObserver.onNext(
      TokenValidityGrpcDTO.newBuilder().setValidity(tokenValidityCmdExe.execute(tokenValidityCmd))
        .build());
    responseObserver.onCompleted();
  }
}
