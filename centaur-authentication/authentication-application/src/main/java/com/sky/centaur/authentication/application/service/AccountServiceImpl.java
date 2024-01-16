package com.sky.centaur.authentication.application.service;

import com.sky.centaur.authentication.application.command.AccountRegisterCmdExe;
import com.sky.centaur.authentication.application.command.query.AccountInnerGetByUsernameExe;
import com.sky.centaur.authentication.client.api.AccountService;
import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.domain.account.Account;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 账户功能实现
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Service
public class AccountServiceImpl implements AccountService, UserDetailsService {

  @Resource
  private AccountInnerGetByUsernameExe accountInnerGetByUsernameExe;

  @Resource
  private AccountRegisterCmdExe accountRegisterCmdExe;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return accountInnerGetByUsernameExe.execute(username);
  }

  @Override
  public Account registered(AccountRegisterCmd accountRegisterCmd) {
    return accountRegisterCmdExe.execute(accountRegisterCmd);
  }
}
