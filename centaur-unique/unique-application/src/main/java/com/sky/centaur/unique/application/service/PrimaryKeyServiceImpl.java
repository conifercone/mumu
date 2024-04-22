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

package com.sky.centaur.unique.application.service;

import com.google.protobuf.Empty;
import com.sky.centaur.unique.application.pk.executor.PrimaryKeySnowflakeGenerateExe;
import com.sky.centaur.unique.client.api.PrimaryKeyService;
import com.sky.centaur.unique.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceImplBase;
import com.sky.centaur.unique.client.api.grpc.SnowflakeResult;
import com.sky.centaur.unique.client.dto.co.PrimaryKeySnowflakeCo;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Service;

/**
 * 主键生成
 *
 * @author kaiyu.shan
 * @since 2024-02-05
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "PrimaryKeyServiceImpl")
public class PrimaryKeyServiceImpl extends PrimaryKeyServiceImplBase implements PrimaryKeyService {

  @Resource
  private PrimaryKeySnowflakeGenerateExe primaryKeySnowflakeGenerateExe;

  @Override
  public PrimaryKeySnowflakeCo snowflake() {
    PrimaryKeySnowflakeCo primaryKeySnowflakeCo = new PrimaryKeySnowflakeCo();
    primaryKeySnowflakeCo.setId(primaryKeySnowflakeGenerateExe.execute());
    return primaryKeySnowflakeCo;
  }

  @Override
  public void snowflake(Empty request, @NotNull StreamObserver<SnowflakeResult> responseObserver) {
    SnowflakeResult build = SnowflakeResult.newBuilder()
        .setId(primaryKeySnowflakeGenerateExe.execute())
        .build();
    responseObserver.onNext(build);
    responseObserver.onCompleted();
  }
}
