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

package baby.mumu.unique.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.unique.application.pk.executor.PrimaryKeySnowflakeGenerateExe;
import baby.mumu.unique.client.api.PrimaryKeyService;
import baby.mumu.unique.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceImplBase;
import baby.mumu.unique.client.api.grpc.SnowflakeResult;
import baby.mumu.unique.client.dto.PrimaryKeySnowflakeDTO;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

/**
 * 主键生成
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "PrimaryKeyServiceImpl")
public class PrimaryKeyServiceImpl extends PrimaryKeyServiceImplBase implements PrimaryKeyService {

  private final PrimaryKeySnowflakeGenerateExe primaryKeySnowflakeGenerateExe;

  @Autowired
  public PrimaryKeyServiceImpl(PrimaryKeySnowflakeGenerateExe primaryKeySnowflakeGenerateExe) {
    this.primaryKeySnowflakeGenerateExe = primaryKeySnowflakeGenerateExe;
  }

  @Override
  public PrimaryKeySnowflakeDTO snowflake() {
    PrimaryKeySnowflakeDTO primaryKeySnowflakeDTO = new PrimaryKeySnowflakeDTO();
    primaryKeySnowflakeDTO.setId(primaryKeySnowflakeGenerateExe.execute());
    return primaryKeySnowflakeDTO;
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void snowflake(Empty request, @NonNull StreamObserver<SnowflakeResult> responseObserver) {
    SnowflakeResult snowflakeResult = SnowflakeResult.newBuilder()
      .setId(primaryKeySnowflakeGenerateExe.execute())
      .build();
    responseObserver.onNext(snowflakeResult);
    responseObserver.onCompleted();
  }
}
