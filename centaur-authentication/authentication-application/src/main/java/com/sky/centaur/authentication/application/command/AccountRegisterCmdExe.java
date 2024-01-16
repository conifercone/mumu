package com.sky.centaur.authentication.application.command;

import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.gateway.AccountGateway;
import com.sky.centaur.authentication.infrastructure.convertor.AccountConvertor;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 账户注册指令执行器
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Component
public class AccountRegisterCmdExe {

  @Resource
  private AccountGateway accountGateway;

  public Account execute(@NotNull AccountRegisterCmd accountRegisterCmd) {
    Account entity = AccountConvertor.toEntity(accountRegisterCmd.getAccountRegisterCo());
    return accountGateway.register(entity);
  }
}
