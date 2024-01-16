package com.sky.centaur.authentication.client.api;

import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.domain.account.Account;

/**
 * 账户功能API
 *
 * @author 单开宇
 * @since 2024-01-15
 */
public interface AccountService {

  Account registered(AccountRegisterCmd accountRegisterCmd);
}
