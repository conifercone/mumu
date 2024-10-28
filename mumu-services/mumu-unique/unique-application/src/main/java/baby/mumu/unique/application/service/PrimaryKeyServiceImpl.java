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
package baby.mumu.unique.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.unique.application.pk.executor.PrimaryKeySnowflakeGenerateExe;
import baby.mumu.unique.client.api.PrimaryKeyService;
import baby.mumu.unique.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceImplBase;
import baby.mumu.unique.client.api.grpc.SnowflakeResult;
import baby.mumu.unique.client.dto.co.PrimaryKeySnowflakeCo;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 主键生成
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "PrimaryKeyServiceImpl")
public class PrimaryKeyServiceImpl extends PrimaryKeyServiceImplBase implements PrimaryKeyService {

  private final PrimaryKeySnowflakeGenerateExe primaryKeySnowflakeGenerateExe;

  @Autowired
  public PrimaryKeyServiceImpl(PrimaryKeySnowflakeGenerateExe primaryKeySnowflakeGenerateExe) {
    this.primaryKeySnowflakeGenerateExe = primaryKeySnowflakeGenerateExe;
  }

  @Override
  public PrimaryKeySnowflakeCo snowflake() {
    PrimaryKeySnowflakeCo primaryKeySnowflakeCo = new PrimaryKeySnowflakeCo();
    primaryKeySnowflakeCo.setId(primaryKeySnowflakeGenerateExe.execute());
    return primaryKeySnowflakeCo;
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void snowflake(Empty request, @NotNull StreamObserver<SnowflakeResult> responseObserver) {
    SnowflakeResult snowflakeResult = SnowflakeResult.newBuilder()
      .setId(primaryKeySnowflakeGenerateExe.execute())
      .build();
    responseObserver.onNext(snowflakeResult);
    responseObserver.onCompleted();
  }
}
