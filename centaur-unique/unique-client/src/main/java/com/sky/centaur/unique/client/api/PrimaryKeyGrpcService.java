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
package com.sky.centaur.unique.client.api;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import com.sky.centaur.unique.client.api.grpc.PrimaryKeyServiceGrpc;
import com.sky.centaur.unique.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceFutureStub;
import com.sky.centaur.unique.client.api.grpc.SnowflakeResult;
import io.grpc.ManagedChannel;
import io.micrometer.observation.annotation.Observed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 主键生成对外提供grpc调用实例
 *
 * @author 单开宇
 * @since 2024-01-25
 */
@Component
@Observed(name = "PrimaryKeyGrpcService")
public class PrimaryKeyGrpcService extends UniqueGrpcService implements DisposableBean,
    InitializingBean {

  private ManagedChannel channel;

  private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryKeyGrpcService.class);

  @Override
  public void afterPropertiesSet() {
    IdGeneratorOptions options = new IdGeneratorOptions();
    YitIdHelper.setIdGenerator(options);
  }

  @Override
  public void destroy() {
    channel.shutdown();
  }

  public Long snowflake() {
    if (channel == null) {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return extracted();
      }).orElseGet(YitIdHelper::nextId);
    } else {
      return extracted();
    }
  }

  private @Nullable Long extracted() {
    PrimaryKeyServiceFutureStub primaryKeyServiceFutureStub = PrimaryKeyServiceGrpc.newFutureStub(
        channel);
    ListenableFuture<SnowflakeResult> snowflake = primaryKeyServiceFutureStub.snowflake(
        Empty.newBuilder().build());
    try {
      return snowflake.get(3, TimeUnit.SECONDS).getId();
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }
}
