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
package baby.mumu.unique.client.api;

import baby.mumu.unique.client.api.grpc.PrimaryKeyServiceGrpc;
import baby.mumu.unique.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceBlockingStub;
import baby.mumu.unique.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceFutureStub;
import baby.mumu.unique.client.api.grpc.SnowflakeResult;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 主键生成对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Observed(name = "PrimaryKeyGrpcService")
public class PrimaryKeyGrpcService extends UniqueGrpcService implements DisposableBean,
    InitializingBean {

  private ManagedChannel channel;

  public PrimaryKeyGrpcService(
      DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void afterPropertiesSet() {
    IdGeneratorOptions options = new IdGeneratorOptions();
    YitIdHelper.setIdGenerator(options);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  public Long snowflake() {
    if (channel == null) {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return snowflakeFromGrpc();
      }).orElseGet(YitIdHelper::nextId);
    } else {
      return snowflakeFromGrpc();
    }
  }

  public Optional<ListenableFuture<SnowflakeResult>> syncSnowflake() {
    if (channel == null) {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncSnowflakeFromGrpc();
      });
    } else {
      return Optional.of(syncSnowflakeFromGrpc());
    }
  }

  private @NotNull Long snowflakeFromGrpc() {
    PrimaryKeyServiceBlockingStub primaryKeyServiceBlockingStub = PrimaryKeyServiceGrpc.newBlockingStub(
        channel);
    return primaryKeyServiceBlockingStub.snowflake(
        Empty.newBuilder().build()).getId();
  }

  private @NotNull ListenableFuture<SnowflakeResult> syncSnowflakeFromGrpc() {
    PrimaryKeyServiceFutureStub primaryKeyServiceFutureStub = PrimaryKeyServiceGrpc.newFutureStub(
        channel);
    return primaryKeyServiceFutureStub.snowflake(
        Empty.newBuilder().build());
  }
}
