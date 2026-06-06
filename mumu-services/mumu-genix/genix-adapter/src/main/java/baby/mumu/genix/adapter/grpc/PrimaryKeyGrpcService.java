/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.genix.adapter.grpc;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.genix.client.api.PrimaryKeyService;
import baby.mumu.genix.client.api.grpc.PrimaryKeyServiceGrpc.PrimaryKeyServiceImplBase;
import baby.mumu.genix.client.api.grpc.SnowflakeResult;
import baby.mumu.genix.client.dto.PrimaryKeySnowflakeDTO;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

/**
 * PrimaryKey gRPC 适配器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@GrpcService
public class PrimaryKeyGrpcService extends PrimaryKeyServiceImplBase {

    private final PrimaryKeyService primaryKeyService;

    @Autowired
    public PrimaryKeyGrpcService(PrimaryKeyService primaryKeyService) {
        this.primaryKeyService = primaryKeyService;
    }

    @Override
    @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
    public void snowflake(Empty request, @NonNull StreamObserver<SnowflakeResult> responseObserver) {
        PrimaryKeySnowflakeDTO result = primaryKeyService.snowflake();
        responseObserver.onNext(SnowflakeResult.newBuilder().setId(result.getId()).build());
        responseObserver.onCompleted();
    }
}
