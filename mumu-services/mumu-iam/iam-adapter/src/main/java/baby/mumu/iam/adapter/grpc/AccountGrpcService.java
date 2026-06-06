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

package baby.mumu.iam.adapter.grpc;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.iam.application.account.convertor.AccountAssemblerConvertor;
import baby.mumu.iam.client.api.AccountService;
import baby.mumu.iam.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountServiceGrpc.AccountServiceImplBase;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

/**
 * Account gRPC 适配器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@GrpcService
public class AccountGrpcService extends AccountServiceImplBase {

    private final AccountService accountService;
    private final AccountAssemblerConvertor accountAssemblerConvertor;

    @Autowired
    public AccountGrpcService(AccountService accountService,
                              AccountAssemblerConvertor accountAssemblerConvertor) {
        this.accountService = accountService;
        this.accountAssemblerConvertor = accountAssemblerConvertor;
    }

    @Override
    @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
    public void queryCurrentLoginAccount(Empty request,
                                         @NonNull StreamObserver<AccountCurrentLoginGrpcDTO> responseObserver) {
        AccountCurrentLoginDTO accountCurrentLoginDTO = accountService.queryCurrentLoginAccount();
        responseObserver.onNext(accountAssemblerConvertor.toAccountCurrentLoginGrpcDTO(accountCurrentLoginDTO)
            .orElse(AccountCurrentLoginGrpcDTO.getDefaultInstance()));
        responseObserver.onCompleted();
    }
}
