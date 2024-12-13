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
import baby.mumu.authentication.client.cmds.AccountAddAddressCmd;
import baby.mumu.authentication.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.cmds.AccountChangePasswordCmd;
import baby.mumu.authentication.client.cmds.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.cmds.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.AccountBasicInfoDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO;
import baby.mumu.authentication.client.dto.AccountFindAllDTO;
import baby.mumu.authentication.client.dto.AccountFindAllSliceDTO;
import baby.mumu.authentication.client.dto.AccountOnlineStatisticsDTO;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
  public void register(
    @Parameter(description = "账户注册指令", required = true) @RequestBody @Valid AccountRegisterCmd accountRegisterCmd) {
    accountService.register(accountRegisterCmd);
  }

  @Operation(summary = "账户基本信息更新")
  @PutMapping("/updateById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(
    @Parameter(description = "根据账户ID更新账户基本信息指令", required = true) @RequestBody @Valid AccountUpdateByIdCmd accountUpdateByIdCmd) {
    accountService.updateById(accountUpdateByIdCmd);
  }

  @Operation(summary = "账户角色更新")
  @PutMapping("/updateRoleById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateRoleById(
    @Parameter(description = "根据账户ID更新账户角色指令", required = true) @RequestBody @Valid AccountUpdateRoleCmd accountUpdateRoleCmd) {
    accountService.updateRoleById(accountUpdateRoleCmd);
  }

  @Operation(summary = "禁用账户")
  @PutMapping("/disable/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(
    @Parameter(description = "账户ID", required = true) @PathVariable(value = "id") Long id) {
    accountService.disable(id);
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
  public AccountCurrentLoginDTO queryCurrentLoginAccount() {
    return accountService.queryCurrentLoginAccount();
  }

  @Operation(summary = "获取在线账户数量等信息")
  @GetMapping("/onlineAccounts")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public AccountOnlineStatisticsDTO onlineAccounts() {
    return accountService.onlineAccounts();
  }

  @Operation(summary = "重置密码")
  @PutMapping("/resetPassword/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void resetPassword(
    @Parameter(description = "账户ID", required = true) @PathVariable(value = "id") Long id) {
    accountService.resetPassword(id);
  }

  @Operation(summary = "重置系统设置")
  @PutMapping("/resetSystemSettingsBySettingsId/{systemSettingsId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void resetSystemSettingsBySettingsId(
    @Parameter(description = "系统设置ID", required = true) @PathVariable(value = "systemSettingsId") String systemSettingsId) {
    accountService.resetSystemSettingsBySettingsId(systemSettingsId);
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
  public ResponseWrapper<Boolean> verifyPassword(
    @ModelAttribute @Valid AccountPasswordVerifyCmd accountPasswordVerifyCmd) {
    return ResponseWrapper.success(accountService.verifyPassword(accountPasswordVerifyCmd));
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
  @PutMapping("/archiveById/{accountId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void archiveById(
    @Parameter(description = "账户ID", required = true) @PathVariable(value = "accountId") Long accountId) {
    accountService.archiveById(accountId);
  }

  @Operation(summary = "根据id从归档中恢复账户")
  @PutMapping("/recoverFromArchiveById/{accountId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverFromArchiveById(
    @Parameter(description = "账户ID", required = true) @PathVariable(value = "accountId") Long accountId) {
    accountService.recoverFromArchiveById(accountId);
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
  @GetMapping("/getAccountBasicInfoById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public AccountBasicInfoDTO getAccountBasicInfoById(
    @Parameter(description = "账户ID", required = true) @PathVariable(value = "id") Long id) {
    return accountService.getAccountBasicInfoById(id);
  }

  @Operation(summary = "根据id下线账户")
  @PostMapping("/offline/{accountId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void offline(
    @Parameter(description = "账户ID", required = true) @PathVariable(value = "accountId") Long accountId) {
    accountService.offline(accountId);
  }

  @Operation(summary = "分页查询账户")
  @GetMapping("/findAll")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public Page<AccountFindAllDTO> findAll(
    @ModelAttribute @Valid AccountFindAllCmd accountFindAllCmd) {
    return accountService.findAll(accountFindAllCmd);
  }

  @Operation(summary = "分页查询账户（不查询总数）")
  @GetMapping("/findAllSlice")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<AccountFindAllSliceDTO> findAllSlice(
    @ModelAttribute @Valid AccountFindAllSliceCmd accountFindAllSliceCmd) {
    return accountService.findAllSlice(accountFindAllSliceCmd);
  }
}
