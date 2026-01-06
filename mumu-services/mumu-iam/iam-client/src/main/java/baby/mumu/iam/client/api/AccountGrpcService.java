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
import baby.mumu.iam.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountServiceGrpc;
import baby.mumu.iam.client.api.grpc.AccountServiceGrpc.AccountServiceBlockingStub;
import baby.mumu.iam.client.api.grpc.AccountServiceGrpc.AccountServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.grpc.client.GrpcChannelFactory;

import java.util.Optional;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

/**
 * 账号对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class AccountGrpcService extends IAMGrpcService implements DisposableBean {

    private ManagedChannel channel;

    public AccountGrpcService(
        DiscoveryClient discoveryClient, GrpcChannelFactory grpcChannelFactory) {
        super(discoveryClient, grpcChannelFactory);
    }

    @Override
    public void destroy() {
        Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public AccountCurrentLoginGrpcDTO queryCurrentLoginAccount() {
        return Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .map(ch -> {
                channel = ch;
                return queryCurrentLoginAccountFromGrpc();
            })
            .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public ListenableFuture<AccountCurrentLoginGrpcDTO> syncQueryCurrentLoginAccount() {
        return Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .map(ch -> {
                channel = ch;
                return syncQueryCurrentLoginAccountFromGrpc();
            })
            .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
    }

    private AccountCurrentLoginGrpcDTO queryCurrentLoginAccountFromGrpc() {
        AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(
            channel);
        return accountServiceBlockingStub
            .queryCurrentLoginAccount(Empty.getDefaultInstance());
    }

    private @NonNull ListenableFuture<AccountCurrentLoginGrpcDTO> syncQueryCurrentLoginAccountFromGrpc() {
        AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
            channel);
        return accountServiceFutureStub
            .queryCurrentLoginAccount(Empty.getDefaultInstance());
    }

}
