package com.sky.centaur.authentication.application.command.query;

import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.infrastructure.convertor.AccountConvertor;
import com.sky.centaur.authentication.infrastructure.gatewayimpl.database.AccountRepository;
import com.sky.centaur.authentication.infrastructure.gatewayimpl.database.dataobject.AccountDo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 根据用户名获取账户信息执行器
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Component
public class AccountInnerGetByUsernameExe {


  @Resource
  private AccountRepository accountRepository;

  public Account execute(String username) {
    AccountDo doByUsername = accountRepository.findAccountDoByUsername(username);
    return AccountConvertor.toEntity(doByUsername);
  }
}
