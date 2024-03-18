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
package com.sky.centaur.authentication.adapter.web;

import com.sky.centaur.authentication.client.api.AccountService;
import com.sky.centaur.authentication.client.dto.AccountDisableCmd;
import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.client.dto.AccountUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import com.sky.centaur.authentication.client.dto.co.AccountDisableCo;
import com.sky.centaur.authentication.client.dto.co.AccountRegisterCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateCo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户相关
 *
 * @author 单开宇
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/account")
@Tag(name = "账户管理")
public class AccountController {

  @Resource
  private AccountService accountService;

  @Operation(summary = "账户注册")
  @PostMapping("/register")
  @ResponseBody
  @API(status = Status.STABLE)
  public AccountRegisterCo register(@RequestBody AccountRegisterCmd accountRegisterCmd) {
    return accountService.register(accountRegisterCmd);
  }

  @Operation(summary = "账户基本信息更新")
  @PutMapping("/updateById")
  @ResponseBody
  @API(status = Status.STABLE)
  public AccountUpdateCo updateById(@RequestBody AccountUpdateCmd accountUpdateCmd) {
    return accountService.updateById(accountUpdateCmd);
  }

  @Operation(summary = "禁用账户")
  @PutMapping("/disable")
  @ResponseBody
  @API(status = Status.STABLE)
  public AccountDisableCo disable(@RequestBody AccountDisableCmd accountDisableCmd) {
    return accountService.disable(accountDisableCmd);
  }

  @Operation(summary = "获取当前登录账户信息")
  @GetMapping("/currentLoginAccount")
  @ResponseBody
  @API(status = Status.STABLE)
  public AccountCurrentLoginQueryCo queryCurrentLoginAccount() {
    return accountService.queryCurrentLoginAccount();
  }
}
