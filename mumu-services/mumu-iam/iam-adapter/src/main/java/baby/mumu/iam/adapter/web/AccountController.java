/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.iam.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.iam.client.api.AccountService;
import baby.mumu.iam.client.cmds.AccountAddAddressCmd;
import baby.mumu.iam.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.iam.client.cmds.AccountChangePasswordCmd;
import baby.mumu.iam.client.cmds.AccountDeleteCurrentCmd;
import baby.mumu.iam.client.cmds.AccountFindAllCmd;
import baby.mumu.iam.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.iam.client.cmds.AccountModifyAddressByAddressIdCmd;
import baby.mumu.iam.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.iam.client.cmds.AccountPasswordVerifyCmd;
import baby.mumu.iam.client.cmds.AccountRegisterCmd;
import baby.mumu.iam.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.iam.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.iam.client.dto.AccountBasicInfoDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO;
import baby.mumu.iam.client.dto.AccountFindAllDTO;
import baby.mumu.iam.client.dto.AccountFindAllSliceDTO;
import baby.mumu.iam.client.dto.AccountNearbyDTO;
import baby.mumu.iam.client.dto.AccountOnlineStatisticsDTO;
import baby.mumu.iam.client.dto.AccountUpdatedDataDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
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
 * 账号相关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@RestController
@Validated
@RequestMapping("/account")
@Tag(name = "账号管理")
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @Operation(summary = "账号注册")
  @PostMapping("/register")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public ResponseWrapper<Long> register(
    @Parameter(description = "账号注册指令", required = true) @RequestBody @Validated AccountRegisterCmd accountRegisterCmd) {
    return ResponseWrapper.success(accountService.register(accountRegisterCmd));
  }

  @Operation(summary = "账号基本信息更新")
  @PutMapping("/updateById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public AccountUpdatedDataDTO updateById(
    @Parameter(description = "根据账号ID更新账号基本信息指令", required = true) @RequestBody @Validated AccountUpdateByIdCmd accountUpdateByIdCmd) {
    return accountService.updateById(accountUpdateByIdCmd);
  }

  @Operation(summary = "账号角色更新")
  @PutMapping("/updateRoleById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateRoleById(
    @Parameter(description = "根据账号ID更新账号角色指令", required = true) @RequestBody @Validated AccountUpdateRoleCmd accountUpdateRoleCmd) {
    accountService.updateRoleById(accountUpdateRoleCmd);
  }

  @Operation(summary = "禁用账号")
  @PutMapping("/disable/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(
    @PathVariable @Parameter(description = "账号ID", required = true) Long id) {
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

  @Operation(summary = "获取当前登录账号信息")
  @GetMapping("/currentLoginAccount")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public AccountCurrentLoginDTO queryCurrentLoginAccount() {
    return accountService.queryCurrentLoginAccount();
  }

  @Operation(summary = "获取在线账号数量等信息")
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
    @PathVariable @Parameter(description = "账号ID", required = true) Long id) {
    accountService.resetPassword(id);
  }

  @Operation(summary = "重置系统设置")
  @PutMapping("/resetSystemSettingsBySettingsId/{systemSettingsId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void resetSystemSettingsBySettingsId(
    @PathVariable @Parameter(description = "系统设置ID", required = true) String systemSettingsId) {
    accountService.resetSystemSettingsBySettingsId(systemSettingsId);
  }

  @Operation(summary = "通过设置id修改系统设置")
  @PutMapping("/modifySystemSettingsBySettingsId")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void modifySystemSettingsBySettingsId(
    @RequestBody @Validated AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd) {
    accountService.modifySystemSettingsBySettingsId(accountModifySystemSettingsBySettingsIdCmd);
  }

  @Operation(summary = "通过地址id修改账号地址")
  @PutMapping("/modifyAddressByAddressId")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.6.0")
  public void modifyAddressByAddressId(
    @RequestBody @Validated AccountModifyAddressByAddressIdCmd accountModifyAddressByAddressIdCmd) {
    accountService.modifyAddressByAddressId(accountModifyAddressByAddressIdCmd);
  }

  @Operation(summary = "添加系统设置")
  @PostMapping("/addSystemSettings")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void addSystemSettings(
    @RequestBody @Validated AccountAddSystemSettingsCmd accountAddSystemSettingsCmd) {
    accountService.addSystemSettings(accountAddSystemSettingsCmd);
  }

  @Operation(summary = "删除当前账号")
  @DeleteMapping("/deleteCurrent")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void deleteCurrent(
    @RequestBody @Validated AccountDeleteCurrentCmd accountDeleteCurrentCmd) {
    accountService.deleteCurrentAccount(accountDeleteCurrentCmd);
  }

  @Operation(summary = "校验账号密码")
  @GetMapping("/verifyPassword")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public ResponseWrapper<Boolean> verifyPassword(
    @ModelAttribute @Validated AccountPasswordVerifyCmd accountPasswordVerifyCmd) {
    return ResponseWrapper.success(accountService.verifyPassword(accountPasswordVerifyCmd));
  }

  @Operation(summary = "修改账号密码")
  @PutMapping("/changePassword")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void changePassword(
    @RequestBody @Validated AccountChangePasswordCmd accountChangePasswordCmd) {
    accountService.changePassword(accountChangePasswordCmd);
  }

  @Operation(summary = "根据id归档账号")
  @PutMapping("/archiveById/{accountId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void archiveById(
    @PathVariable @Parameter(description = "账号ID", required = true) Long accountId) {
    accountService.archiveById(accountId);
  }

  @Operation(summary = "根据id从归档中恢复账号")
  @PutMapping("/recoverFromArchiveById/{accountId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverFromArchiveById(
    @PathVariable @Parameter(description = "账号ID", required = true) Long accountId) {
    accountService.recoverFromArchiveById(accountId);
  }

  @Operation(summary = "账号添加地址")
  @PostMapping("/addAddress")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public void addAddress(
    @RequestBody @Validated AccountAddAddressCmd accountAddAddressCmd) {
    accountService.addAddress(accountAddAddressCmd);
  }

  @Operation(summary = "根据id查询账号基本信息")
  @GetMapping("/getAccountBasicInfoById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public AccountBasicInfoDTO getAccountBasicInfoById(
    @PathVariable @Parameter(description = "账号ID", required = true) Long id) {
    return accountService.getAccountBasicInfoById(id);
  }

  @Operation(summary = "根据id下线账号")
  @PostMapping("/offline/{accountId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public void offline(
    @PathVariable @Parameter(description = "账号ID", required = true) Long accountId) {
    accountService.offline(accountId);
  }

  @Operation(summary = "分页查询账号")
  @GetMapping("/findAll")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public Page<AccountFindAllDTO> findAll(
    @ModelAttribute @Validated AccountFindAllCmd accountFindAllCmd) {
    return accountService.findAll(accountFindAllCmd);
  }

  @Operation(summary = "分页查询账号（不查询总数）")
  @GetMapping("/findAllSlice")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<AccountFindAllSliceDTO> findAllSlice(
    @ModelAttribute @Validated AccountFindAllSliceCmd accountFindAllSliceCmd) {
    return accountService.findAllSlice(accountFindAllSliceCmd);
  }

  @Operation(summary = "附近的账号")
  @GetMapping("/nearby/{radiusInMeters}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.6.0")
  public ResponseWrapper<List<AccountNearbyDTO>> nearby(
    @PathVariable @Min(1) double radiusInMeters) {
    return ResponseWrapper.success(accountService.nearby(radiusInMeters));
  }

  @Operation(summary = "当前账号设置默认地址")
  @PutMapping("/setDefaultAddress/{addressId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.6.0")
  public void setDefaultAddress(
    @PathVariable @NotBlank String addressId) {
    accountService.setDefaultAddress(addressId);
  }

  @Operation(summary = "删除指定账号地址")
  @DeleteMapping("/address/{addressId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.6.0")
  public void deleteAddress(
    @PathVariable @NotBlank String addressId) {
    accountService.deleteAddress(addressId);
  }

  @Operation(summary = "当前账号设置默认系统设置")
  @PutMapping("/setDefaultSystemSettings/{systemSettingsId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.6.0")
  public void setDefaultSystemSettings(
    @PathVariable @NotBlank String systemSettingsId) {
    accountService.setDefaultSystemSettings(systemSettingsId);
  }

  @Operation(summary = "删除指定账号系统设置")
  @DeleteMapping("/systemSettings/{systemSettingsId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.6.0")
  public void deleteSystemSettings(
    @PathVariable @NotBlank String systemSettingsId) {
    accountService.deleteSystemSettings(systemSettingsId);
  }
}
