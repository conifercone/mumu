/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
import baby.mumu.authentication.client.api.grpc.TokenValidityGrpcCo;
import baby.mumu.authentication.client.dto.TokenValidityCmd;
import baby.mumu.authentication.client.dto.co.TokenValidityCo;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * token service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "TokenServiceImpl")
public class TokenServiceImpl extends TokenServiceImplBase implements TokenService {


  private final TokenValidityCmdExe tokenValidityCmdExe;

  @Autowired
  public TokenServiceImpl(TokenValidityCmdExe tokenValidityCmdExe) {
    this.tokenValidityCmdExe = tokenValidityCmdExe;
  }

  @Override
  public TokenValidityCo validity(TokenValidityCmd tokenValidityCmd) {
    return tokenValidityCmdExe.execute(tokenValidityCmd);
  }

  @Override
  public void validity(TokenValidityGrpcCmd request,
      StreamObserver<TokenValidityGrpcCo> responseObserver) {
    TokenValidityCmd tokenValidityCmd = new TokenValidityCmd();
    TokenValidityCo tokenValidityCo = getTokenValidityCo(request);
    tokenValidityCmd.setTokenValidityCo(tokenValidityCo);
    TokenValidityGrpcCo tokenValidityGrpcCo = request.getTokenValidityCo();
    try {
      TokenValidityCo execute = tokenValidityCmdExe.execute(tokenValidityCmd);
      tokenValidityGrpcCo = tokenValidityGrpcCo.toBuilder().setValidity(execute.isValidity())
          .setToken(tokenValidityCo.getToken()).build();
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(tokenValidityGrpcCo);
    responseObserver.onCompleted();
  }

  @NotNull
  private static TokenValidityCo getTokenValidityCo(
      @NotNull TokenValidityGrpcCmd request) {
    TokenValidityCo tokenValidityCo = new TokenValidityCo();
    TokenValidityGrpcCo tokenValidityGrpcCo = request.getTokenValidityCo();
    tokenValidityCo.setToken(tokenValidityGrpcCo.getToken());
    return tokenValidityCo;
  }
}
