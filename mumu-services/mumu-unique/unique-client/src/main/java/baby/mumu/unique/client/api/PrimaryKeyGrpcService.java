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
package baby.mumu.unique.client.api;

import baby.mumu.unique.client.api.grpc.PrimaryKeyServiceGrpc;
import baby.mumu.unique.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceBlockingStub;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
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
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return snowflakeFromGrpc();
      })
      .orElseGet(YitIdHelper::nextId);
  }

  private @NotNull Long snowflakeFromGrpc() {
    PrimaryKeyServiceBlockingStub primaryKeyServiceBlockingStub = PrimaryKeyServiceGrpc.newBlockingStub(
      channel);
    return primaryKeyServiceBlockingStub.snowflake(
      Empty.newBuilder().build()).getId();
  }
}
