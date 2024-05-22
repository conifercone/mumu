package com.sky.centaur.authentication.application.service;

import com.sky.centaur.authentication.application.token.executor.TokenValidityCmdExe;
import com.sky.centaur.authentication.client.api.TokenService;
import com.sky.centaur.authentication.client.api.grpc.TokenServiceGrpc.TokenServiceImplBase;
import com.sky.centaur.authentication.client.api.grpc.TokenValidityGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.TokenValidityGrpcCo;
import com.sky.centaur.authentication.client.dto.TokenValidityCmd;
import com.sky.centaur.authentication.client.dto.co.TokenValidityCo;
import com.sky.centaur.basis.exception.CentaurException;
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
 * @author kaiyu.shan
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
    } catch (CentaurException e) {
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
