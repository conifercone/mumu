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
import baby.mumu.iam.application.role.convertor.RoleAssemblerConvertor;
import baby.mumu.iam.client.api.RoleService;
import baby.mumu.iam.client.api.grpc.PageOfRoleFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.PageOfRoleFindAllGrpcDTO.Builder;
import baby.mumu.iam.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.RoleFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleServiceGrpc.RoleServiceImplBase;
import baby.mumu.iam.client.dto.RoleFindAllDTO;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;

/**
 * Role gRPC 适配器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@GrpcService
public class RoleGrpcService extends RoleServiceImplBase {

    private final RoleService roleService;
    private final RoleAssemblerConvertor roleAssemblerConvertor;

    @Autowired
    public RoleGrpcService(RoleService roleService,
                           RoleAssemblerConvertor roleAssemblerConvertor) {
        this.roleService = roleService;
        this.roleAssemblerConvertor = roleAssemblerConvertor;
    }

    @Override
    @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
    public void findAll(RoleFindAllGrpcCmd request,
                        StreamObserver<PageOfRoleFindAllGrpcDTO> responseObserver) {
        roleAssemblerConvertor.toRoleFindAllCmd(request).ifPresentOrElse(roleFindAllCmdNotNull -> {
            Builder builder = PageOfRoleFindAllGrpcDTO.newBuilder();
            Page<RoleFindAllDTO> roleFindAllCos = roleService.findAll(
                roleFindAllCmdNotNull);
            List<RoleGrpcDTO> findAllGrpcCos = roleFindAllCos.getContent().stream()
                .flatMap(roleFindAllCo -> roleAssemblerConvertor.toRoleGrpcDTO(roleFindAllCo).stream())
                .toList();
            builder.addAllContent(findAllGrpcCos);
            builder.setTotalPages(roleFindAllCos.getTotalPages());
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }, () -> {
            responseObserver.onNext(PageOfRoleFindAllGrpcDTO.getDefaultInstance());
            responseObserver.onCompleted();
        });
    }

    @Override
    public void findById(Int64Value request, StreamObserver<RoleFindByIdGrpcDTO> responseObserver) {
        Optional.ofNullable(request).filter(Int64Value::isInitialized).map(Int64Value::getValue)
            .map(roleService::findById).flatMap(roleAssemblerConvertor::toRoleFindByIdGrpcDTO)
            .ifPresentOrElse((roleFindByIdGrpcDTO) -> {
                responseObserver.onNext(roleFindByIdGrpcDTO);
                responseObserver.onCompleted();
            }, () -> {
                throw new ApplicationException(ResponseCode.ROLE_DOES_NOT_EXIST);
            });
    }
}
