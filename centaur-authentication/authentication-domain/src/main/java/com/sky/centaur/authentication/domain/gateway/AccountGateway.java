package com.sky.centaur.authentication.domain.gateway;

import com.sky.centaur.authentication.domain.account.Account;

/**
 * 用户领域网关
 *
 * @author 单开宇
 * @since 2024-01-16
 */
public interface AccountGateway {

  Account register(Account account);
}
