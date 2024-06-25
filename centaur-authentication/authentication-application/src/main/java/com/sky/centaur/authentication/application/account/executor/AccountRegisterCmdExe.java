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
package com.sky.centaur.authentication.application.account.executor;

import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import com.sky.centaur.authentication.infrastructure.account.convertor.AccountConvertor;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.client.api.CaptchaGrpcService;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCo;
import io.micrometer.observation.annotation.Observed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 账户注册指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountRegisterCmdExe")
public class AccountRegisterCmdExe {

  private final AccountGateway accountGateway;
  private final CaptchaGrpcService captchaGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(AccountRegisterCmdExe.class);

  @Autowired
  public AccountRegisterCmdExe(AccountGateway accountGateway,
      CaptchaGrpcService captchaGrpcService) {
    this.accountGateway = accountGateway;
    this.captchaGrpcService = captchaGrpcService;
  }

  public void execute(@NotNull AccountRegisterCmd accountRegisterCmd) {
    Assert.notNull(accountRegisterCmd, "AccountRegisterCmd cannot be null");
    try {
      if (!captchaGrpcService.verifySimpleCaptcha(
          SimpleCaptchaVerifyGrpcCmd.newBuilder().setSimpleCaptchaVerifyGrpcCo(
              SimpleCaptchaVerifyGrpcCo.newBuilder()
                  .setId(Int64Value.of(accountRegisterCmd.getCaptchaId())).setSource(
                      StringValue.of(accountRegisterCmd.getCaptcha())).build()).build()).getResult()) {
        throw new CentaurException(ResultCode.CAPTCHA_INCORRECT);
      }
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      LOGGER.error(ResultCode.CAPTCHA_VERIFICATION_EXCEPTION.getResultMsg(), e);
      throw new CentaurException(ResultCode.CAPTCHA_VERIFICATION_EXCEPTION);
    }
    AccountConvertor.toEntity(accountRegisterCmd.getAccountRegisterCo())
        .ifPresent(accountGateway::register);
  }
}
