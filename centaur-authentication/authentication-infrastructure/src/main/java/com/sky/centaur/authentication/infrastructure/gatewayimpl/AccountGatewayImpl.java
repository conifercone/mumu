package com.sky.centaur.authentication.infrastructure.gatewayimpl;

import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.gateway.AccountGateway;
import com.sky.centaur.authentication.infrastructure.convertor.AccountConvertor;
import com.sky.centaur.authentication.infrastructure.gatewayimpl.database.AccountRepository;
import com.sky.centaur.authentication.infrastructure.gatewayimpl.database.dataobject.AccountDo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 用户领域网关实现
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Component
public class AccountGatewayImpl implements AccountGateway {

  @Resource
  private AccountRepository accountRepository;

  @Override
  public Account register(Account account) {
    AccountDo save = accountRepository.save(AccountConvertor.toDataObject(account));
    return AccountConvertor.toEntity(save);
  }
}
