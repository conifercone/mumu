/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.authentication.adapter.web;

import baby.mumu.authentication.client.api.AccountService;
import baby.mumu.authentication.client.dto.AccountAddAddressCmd;
import baby.mumu.authentication.client.dto.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.dto.AccountArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AccountBasicInfoByIdCmd;
import baby.mumu.authentication.client.dto.AccountChangePasswordCmd;
import baby.mumu.authentication.client.dto.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.dto.AccountDisableCmd;
import baby.mumu.authentication.client.dto.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.dto.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.dto.AccountRecoverFromArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AccountRegisterCmd;
import baby.mumu.authentication.client.dto.AccountResetPasswordCmd;
import baby.mumu.authentication.client.dto.AccountResetSystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.co.AccountBasicInfoCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo;
import baby.mumu.authentication.client.dto.co.AccountOnlineStatisticsCo;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping("/account")
@Tag(name = "账户管理")
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @Operation(summary = "账户注册")
  @PostMapping("/register")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void register(@RequestBody @Valid AccountRegisterCmd accountRegisterCmd) {
    accountService.register(accountRegisterCmd);
  }

  @Operation(summary = "账户基本信息更新")
  @PutMapping("/updateById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@RequestBody @Valid AccountUpdateByIdCmd accountUpdateByIdCmd) {
    accountService.updateById(accountUpdateByIdCmd);
  }

  @Operation(summary = "账户角色更新")
  @PutMapping("/updateRoleById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateRoleById(
      @RequestBody @Valid AccountUpdateRoleCmd accountUpdateRoleCmd) {
    accountService.updateRoleById(accountUpdateRoleCmd);
  }

  @Operation(summary = "禁用账户")
  @PutMapping("/disable")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(@RequestBody @Valid AccountDisableCmd accountDisableCmd) {
    accountService.disable(accountDisableCmd);
  }

  @Operation(summary = "退出登录")
  @PostMapping("/logout")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void logout() {
    accountService.logout();
  }

  @Operation(summary = "获取当前登录账户信息")
  @GetMapping("/currentLoginAccount")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public AccountCurrentLoginCo queryCurrentLoginAccount() {
    return accountService.queryCurrentLoginAccount();
  }

  @Operation(summary = "获取在线账户数量等信息")
  @GetMapping("/onlineAccounts")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public AccountOnlineStatisticsCo onlineAccounts() {
    return accountService.onlineAccounts();
  }

  @Operation(summary = "重置密码")
  @PutMapping("/resetPassword")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void resetPassword(
      @RequestBody @Valid AccountResetPasswordCmd accountResetPasswordCmd) {
    accountService.resetPassword(accountResetPasswordCmd);
  }

  @Operation(summary = "重置系统设置")
  @PutMapping("/resetSystemSettingsBySettingsId")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void resetSystemSettingsBySettingsId(
      @RequestBody @Valid AccountResetSystemSettingsBySettingsIdCmd accountResetSystemSettingsBySettingsIdCmd) {
    accountService.resetSystemSettingsBySettingsId(accountResetSystemSettingsBySettingsIdCmd);
  }

  @Operation(summary = "通过设置id修改系统设置")
  @PutMapping("/modifySystemSettingsBySettingsId")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void modifySystemSettingsBySettingsId(
      @RequestBody @Valid AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd) {
    accountService.modifySystemSettingsBySettingsId(accountModifySystemSettingsBySettingsIdCmd);
  }

  @Operation(summary = "添加系统设置")
  @PostMapping("/addSystemSettings")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void addSystemSettings(
      @RequestBody @Valid AccountAddSystemSettingsCmd accountAddSystemSettingsCmd) {
    accountService.addSystemSettings(accountAddSystemSettingsCmd);
  }

  @Operation(summary = "删除当前账户")
  @DeleteMapping("/deleteCurrent")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void deleteCurrent(@RequestBody @Valid AccountDeleteCurrentCmd accountDeleteCurrentCmd) {
    accountService.deleteCurrentAccount(accountDeleteCurrentCmd);
  }

  @Operation(summary = "校验账户密码")
  @GetMapping("/verifyPassword")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public ResultResponse<Boolean> verifyPassword(
      @RequestBody @Valid AccountPasswordVerifyCmd accountPasswordVerifyCmd) {
    return ResultResponse.success(accountService.verifyPassword(accountPasswordVerifyCmd));
  }

  @Operation(summary = "修改账户密码")
  @PutMapping("/changePassword")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void changePassword(
      @RequestBody @Valid AccountChangePasswordCmd accountChangePasswordCmd) {
    accountService.changePassword(accountChangePasswordCmd);
  }

  @Operation(summary = "根据id归档账户")
  @PutMapping("/archiveById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void archiveById(
      @RequestBody @Valid AccountArchiveByIdCmd accountArchiveByIdCmd) {
    accountService.archiveById(accountArchiveByIdCmd);
  }

  @Operation(summary = "根据id从归档中恢复账户")
  @PutMapping("/recoverFromArchiveById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverFromArchiveById(
      @RequestBody @Valid AccountRecoverFromArchiveByIdCmd accountRecoverFromArchiveByIdCmd) {
    accountService.recoverFromArchiveById(accountRecoverFromArchiveByIdCmd);
  }

  @Operation(summary = "账户添加地址")
  @PostMapping("/addAddress")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public void addAddress(
      @RequestBody @Valid AccountAddAddressCmd accountAddAddressCmd) {
    accountService.addAddress(accountAddAddressCmd);
  }

  @Operation(summary = "根据id查询账户基本信息")
  @GetMapping("/getAccountBasicInfoById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public AccountBasicInfoCo getAccountBasicInfoById(
      @RequestBody @Valid AccountBasicInfoByIdCmd accountBasicInfoByIdCmd) {
    return accountService.getAccountBasicInfoById(accountBasicInfoByIdCmd);
  }
}
