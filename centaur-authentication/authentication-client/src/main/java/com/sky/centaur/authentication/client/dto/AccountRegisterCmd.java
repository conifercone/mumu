package com.sky.centaur.authentication.client.dto;

import com.sky.centaur.authentication.client.dto.clientobject.AccountRegisterCo;
import lombok.Data;

/**
 * 账户注册指令
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Data
public class AccountRegisterCmd {

  private AccountRegisterCo accountRegisterCo;
}
