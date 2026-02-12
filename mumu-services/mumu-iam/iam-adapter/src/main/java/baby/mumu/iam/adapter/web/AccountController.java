/*
 * Copyright (c) 2024-2026, the original author or authors.
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
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "账号注册",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "账号注册命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountRegisterCmd.class))))
    @PostMapping("/register")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public ResponseWrapper<Long> register(
        @RequestBody @Validated AccountRegisterCmd accountRegisterCmd) {
        return ResponseWrapper.success(accountService.register(accountRegisterCmd));
    }

    @Operation(summary = "账号基本信息更新",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "账号基础信息更新命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountUpdateByIdCmd.class))))
    @PutMapping("/updateById")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public AccountUpdatedDataDTO updateById(
        @RequestBody @Validated AccountUpdateByIdCmd accountUpdateByIdCmd) {
        return accountService.updateById(accountUpdateByIdCmd);
    }

    @Operation(summary = "账号角色更新",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "账号角色更新命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountUpdateRoleCmd.class))))
    @PutMapping("/updateRoleById")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void updateRoleById(
        @RequestBody @Validated AccountUpdateRoleCmd accountUpdateRoleCmd) {
        accountService.updateRoleById(accountUpdateRoleCmd);
    }

    @Operation(summary = "禁用账号",
        parameters = {
            @Parameter(name = "id", description = "账号ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/disable/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void disable(
        @PathVariable Long id) {
        accountService.disable(id);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public void logout() {
        accountService.logout();
    }

    @Operation(summary = "获取当前登录账号信息")
    @GetMapping("/currentLoginAccount")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public AccountCurrentLoginDTO queryCurrentLoginAccount() {
        return accountService.queryCurrentLoginAccount();
    }

    @Operation(summary = "获取在线账号数量等信息")
    @GetMapping("/onlineAccounts")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public AccountOnlineStatisticsDTO onlineAccounts() {
        return accountService.onlineAccounts();
    }

    @Operation(summary = "重置密码",
        parameters = {
            @Parameter(name = "id", description = "账号ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/resetPassword/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void resetPassword(
        @PathVariable Long id) {
        accountService.resetPassword(id);
    }

    @Operation(summary = "重置系统设置",
        parameters = {
            @Parameter(name = "systemSettingsId", description = "系统设置ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/resetSystemSettingsBySettingsId/{systemSettingsId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public void resetSystemSettingsBySettingsId(
        @PathVariable String systemSettingsId) {
        accountService.resetSystemSettingsBySettingsId(systemSettingsId);
    }

    @Operation(summary = "通过设置id修改系统设置",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "按系统设置ID修改系统设置命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountModifySystemSettingsBySettingsIdCmd.class))))
    @PutMapping("/modifySystemSettingsBySettingsId")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public void modifySystemSettingsBySettingsId(
        @RequestBody @Validated AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd) {
        accountService.modifySystemSettingsBySettingsId(accountModifySystemSettingsBySettingsIdCmd);
    }

    @Operation(summary = "通过地址id修改账号地址",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "按地址ID修改账号地址命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountModifyAddressByAddressIdCmd.class))))
    @PutMapping("/modifyAddressByAddressId")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.6.0")
    public void modifyAddressByAddressId(
        @RequestBody @Validated AccountModifyAddressByAddressIdCmd accountModifyAddressByAddressIdCmd) {
        accountService.modifyAddressByAddressId(accountModifyAddressByAddressIdCmd);
    }

    @Operation(summary = "添加系统设置",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "账号新增系统设置命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountAddSystemSettingsCmd.class))))
    @PostMapping("/addSystemSettings")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public void addSystemSettings(
        @RequestBody @Validated AccountAddSystemSettingsCmd accountAddSystemSettingsCmd) {
        accountService.addSystemSettings(accountAddSystemSettingsCmd);
    }

    @Operation(summary = "删除当前账号",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "删除当前账号命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountDeleteCurrentCmd.class))))
    @DeleteMapping("/deleteCurrent")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void deleteCurrent(
        @RequestBody @Validated AccountDeleteCurrentCmd accountDeleteCurrentCmd) {
        accountService.deleteCurrentAccount(accountDeleteCurrentCmd);
    }

    @Operation(summary = "校验账号密码")
    @GetMapping("/verifyPassword")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public ResponseWrapper<Boolean> verifyPassword(
        @ParameterObject @ModelAttribute @Validated AccountPasswordVerifyCmd accountPasswordVerifyCmd) {
        return ResponseWrapper.success(accountService.verifyPassword(accountPasswordVerifyCmd));
    }

    @Operation(summary = "修改账号密码",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "账号修改密码命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountChangePasswordCmd.class))))
    @PutMapping("/changePassword")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void changePassword(
        @RequestBody @Validated AccountChangePasswordCmd accountChangePasswordCmd) {
        accountService.changePassword(accountChangePasswordCmd);
    }

    @Operation(summary = "根据id归档账号",
        parameters = {
            @Parameter(name = "accountId", description = "账号ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/archiveById/{accountId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void archiveById(
        @PathVariable Long accountId) {
        accountService.archiveById(accountId);
    }

    @Operation(summary = "根据id从归档中恢复账号",
        parameters = {
            @Parameter(name = "accountId", description = "账号ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/recoverFromArchiveById/{accountId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void recoverFromArchiveById(
        @PathVariable Long accountId) {
        accountService.recoverFromArchiveById(accountId);
    }

    @Operation(summary = "账号添加地址",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "账号新增地址命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AccountAddAddressCmd.class))))
    @PostMapping("/addAddress")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public void addAddress(
        @RequestBody @Validated AccountAddAddressCmd accountAddAddressCmd) {
        accountService.addAddress(accountAddAddressCmd);
    }

    @Operation(summary = "根据id查询账号基本信息",
        parameters = {
            @Parameter(name = "id", description = "账号ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/getAccountBasicInfoById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public AccountBasicInfoDTO getAccountBasicInfoById(
        @PathVariable Long id) {
        return accountService.getAccountBasicInfoById(id);
    }

    @Operation(summary = "根据id下线账号",
        parameters = {
            @Parameter(name = "accountId", description = "账号ID", required = true, in = ParameterIn.PATH)
        })
    @PostMapping("/offline/{accountId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public void offline(
        @PathVariable Long accountId) {
        accountService.offline(accountId);
    }

    @Operation(summary = "分页查询账号")
    @GetMapping("/findAll")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public Page<AccountFindAllDTO> findAll(
        @ParameterObject @ModelAttribute @Validated AccountFindAllCmd accountFindAllCmd) {
        return accountService.findAll(accountFindAllCmd);
    }

    @Operation(summary = "分页查询账号（不查询总数）")
    @GetMapping("/findAllSlice")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public Slice<AccountFindAllSliceDTO> findAllSlice(
        @ParameterObject @ModelAttribute @Validated AccountFindAllSliceCmd accountFindAllSliceCmd) {
        return accountService.findAllSlice(accountFindAllSliceCmd);
    }

    @Operation(summary = "附近的账号",
        parameters = {
            @Parameter(name = "radiusInMeters", description = "半径（米）", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/nearby/{radiusInMeters}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.6.0")
    public ResponseWrapper<List<AccountNearbyDTO>> nearby(
        @PathVariable @Min(1) double radiusInMeters) {
        return ResponseWrapper.success(accountService.nearby(radiusInMeters));
    }

    @Operation(summary = "当前账号设置默认地址",
        parameters = {
            @Parameter(name = "addressId", description = "地址ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/setDefaultAddress/{addressId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.6.0")
    public void setDefaultAddress(
        @PathVariable @NotBlank String addressId) {
        accountService.setDefaultAddress(addressId);
    }

    @Operation(summary = "删除指定账号地址",
        parameters = {
            @Parameter(name = "addressId", description = "地址ID", required = true, in = ParameterIn.PATH)
        })
    @DeleteMapping("/address/{addressId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.6.0")
    public void deleteAddress(
        @PathVariable @NotBlank String addressId) {
        accountService.deleteAddress(addressId);
    }

    @Operation(summary = "当前账号设置默认系统设置",
        parameters = {
            @Parameter(name = "systemSettingsId", description = "系统设置ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/setDefaultSystemSettings/{systemSettingsId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.6.0")
    public void setDefaultSystemSettings(
        @PathVariable @NotBlank String systemSettingsId) {
        accountService.setDefaultSystemSettings(systemSettingsId);
    }

    @Operation(summary = "删除指定账号系统设置",
        parameters = {
            @Parameter(name = "systemSettingsId", description = "系统设置ID", required = true, in = ParameterIn.PATH)
        })
    @DeleteMapping("/systemSettings/{systemSettingsId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.6.0")
    public void deleteSystemSettings(
        @PathVariable @NotBlank String systemSettingsId) {
        accountService.deleteSystemSettings(systemSettingsId);
    }
}
