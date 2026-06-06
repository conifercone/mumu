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
import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.iam.application.permission.convertor.PermissionAssemblerConvertor;
import baby.mumu.iam.client.api.PermissionService;
import baby.mumu.iam.client.api.grpc.PageOfPermissionFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.PageOfPermissionFindAllGrpcDTO.Builder;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionServiceGrpc.PermissionServiceImplBase;
import baby.mumu.iam.client.dto.PermissionFindAllDTO;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;

/**
 * Permission gRPC 适配器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@GrpcService
public class PermissionGrpcService extends PermissionServiceImplBase {

    private final PermissionService permissionService;
    private final PermissionAssemblerConvertor permissionAssemblerConvertor;

    @Autowired
    public PermissionGrpcService(PermissionService permissionService,
                                 PermissionAssemblerConvertor permissionAssemblerConvertor) {
        this.permissionService = permissionService;
        this.permissionAssemblerConvertor = permissionAssemblerConvertor;
    }

    @Override
    public void findById(Int64Value request,
                         StreamObserver<PermissionFindByIdGrpcDTO> responseObserver) {
        Runnable runnable = () -> {
            throw new ApplicationException(ResponseCode.PERMISSION_DOES_NOT_EXIST);
        };
        Optional.ofNullable(request).filter(Int64Value::isInitialized).ifPresentOrElse(
            (id) -> permissionAssemblerConvertor.toPermissionFindByIdGrpcDTO(
                    permissionService.findById(id.getValue()))
                .ifPresentOrElse((permissionFindByIdGrpcDTO) -> {
                    responseObserver.onNext(permissionFindByIdGrpcDTO);
                    responseObserver.onCompleted();
                }, runnable), runnable);
    }

    @Override
    @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
    public void findAll(PermissionFindAllGrpcCmd request,
                        StreamObserver<PageOfPermissionFindAllGrpcDTO> responseObserver) {
        permissionAssemblerConvertor.toPermissionFindAllCmd(request)
            .ifPresentOrElse((permissionFindAllCmdNotNull) -> {
                Builder builder = PageOfPermissionFindAllGrpcDTO.newBuilder();
                Page<PermissionFindAllDTO> permissionFindAllCos = permissionService.findAll(
                    permissionFindAllCmdNotNull);
                List<PermissionGrpcDTO> findAllGrpcCos = permissionFindAllCos.getContent().stream()
                    .flatMap(
                        permissionFindAllCo -> permissionAssemblerConvertor.toPermissionGrpcDTO(
                                permissionFindAllCo)
                            .stream()).toList();
                builder.addAllContent(findAllGrpcCos);
                builder.setTotalPages(permissionFindAllCos.getTotalPages());
                responseObserver.onNext(builder.build());
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onNext(PageOfPermissionFindAllGrpcDTO.getDefaultInstance());
                responseObserver.onCompleted();
            });
    }
}
