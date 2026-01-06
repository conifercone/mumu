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

package baby.mumu.iam.client.api;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.iam.client.api.grpc.TokenServiceGrpc;
import baby.mumu.iam.client.api.grpc.TokenServiceGrpc.TokenServiceBlockingStub;
import baby.mumu.iam.client.api.grpc.TokenValidityGrpcCmd;
import baby.mumu.iam.client.api.grpc.TokenValidityGrpcDTO;
import io.grpc.ManagedChannel;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.grpc.client.GrpcChannelFactory;

import java.util.Optional;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

/**
 * token对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class TokenGrpcService extends IAMGrpcService implements DisposableBean {

    private ManagedChannel channel;

    public TokenGrpcService(
        DiscoveryClient discoveryClient,
        GrpcChannelFactory grpcChannelFactory) {
        super(discoveryClient, grpcChannelFactory);
    }

    @Override
    public void destroy() {
        Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public TokenValidityGrpcDTO validity(TokenValidityGrpcCmd tokenValidityGrpcCmd) {
        return Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .map(ch -> {
                channel = ch;
                return validityFromGrpc(tokenValidityGrpcCmd);
            })
            .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
    }

    private @Nullable TokenValidityGrpcDTO validityFromGrpc(
        TokenValidityGrpcCmd tokenValidityGrpcCmd) {
        TokenServiceBlockingStub tokenServiceBlockingStub = TokenServiceGrpc.newBlockingStub(channel);
        return tokenServiceBlockingStub.validity(tokenValidityGrpcCmd);
    }

}
