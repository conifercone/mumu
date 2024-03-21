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
package com.sky.centaur.authentication.client.api;

import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountServiceGrpc;
import com.sky.centaur.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceFutureStub;
import io.grpc.ManagedChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * 账户对外提供grpc调用实例
 *
 * @author 单开宇
 * @since 2024-01-25
 */
@Component
public class AccountGrpcService extends AuthenticationGrpcService implements DisposableBean {

  private ManagedChannel channel;

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountGrpcService.class);

  @Override
  public void destroy() {
    channel.shutdown();
  }

  @API(status = Status.STABLE)
  public AccountRegisterGrpcCo register(AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    if (channel == null) {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return extracted(accountRegisterGrpcCmd);
      }).orElse(null);
    } else {
      return extracted(accountRegisterGrpcCmd);
    }

  }

  private @Nullable AccountRegisterGrpcCo extracted(AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
        channel);
    try {
      return accountServiceFutureStub.register(accountRegisterGrpcCmd).get(3, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }

}
