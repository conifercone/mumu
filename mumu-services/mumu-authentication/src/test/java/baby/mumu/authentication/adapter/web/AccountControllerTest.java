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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import baby.mumu.authentication.client.dto.AccountChangePasswordCmd;
import baby.mumu.authentication.client.dto.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.dto.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.dto.AccountRegisterCmd;
import baby.mumu.authentication.client.dto.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo.AccountAddressRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateByIdCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateRoleCo;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import baby.mumu.unique.client.api.CaptchaGrpcService;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import java.time.LocalDate;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户相关web接口单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
public class AccountControllerTest {

  private final MockMvc mockMvc;
  private final CaptchaGrpcService captchaGrpcService;
  private final ObjectMapper objectMapper;

  @Autowired
  public AccountControllerTest(MockMvc mockMvc, CaptchaGrpcService captchaGrpcService,
    ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.captchaGrpcService = captchaGrpcService;
    this.objectMapper = objectMapper;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void register() throws Exception {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
      .setLength(Int32Value.of(4))
      .setTtl(Int64Value.of(500)).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    AccountRegisterCmd accountRegisterCmd = new AccountRegisterCmd();
    accountRegisterCmd.setCaptchaId(simpleCaptchaGeneratedGrpcCo.getId().getValue());
    accountRegisterCmd.setCaptcha(simpleCaptchaGeneratedGrpcCo.getTarget().getValue());
    AccountRegisterCo accountRegisterCo = getAccountRegisterCo();
    accountRegisterCmd.setAccountRegisterCo(accountRegisterCo);
    mockMvc.perform(MockMvcRequestBuilders
        .post("/account/register").with(csrf())
        .content(objectMapper.writeValueAsString(accountRegisterCmd).getBytes())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  private static @NotNull AccountRegisterCo getAccountRegisterCo() {
    AccountRegisterCo accountRegisterCo = new AccountRegisterCo();
    accountRegisterCo.setId(31241232131L);
    accountRegisterCo.setUsername("test1");
    accountRegisterCo.setPassword("test1");
    accountRegisterCo.setRoleCodes(Collections.singletonList("admin"));
    accountRegisterCo.setAvatarUrl("https://github.com/users/conifercone");
    accountRegisterCo.setPhone("13031723736");
    accountRegisterCo.setSex(SexEnum.MALE);
    accountRegisterCo.setLanguage(LanguageEnum.ZH);
    accountRegisterCo.setTimezone("Asia/Shanghai");
    accountRegisterCo.setEmail("547913250@qq.com");
    accountRegisterCo.setBirthday(LocalDate.of(1995, 8, 2));
    AccountAddressRegisterCo accountAddressRegisterCo = new AccountAddressRegisterCo();
    accountAddressRegisterCo.setStreet("历城区");
    accountAddressRegisterCo.setCity("济南市");
    accountAddressRegisterCo.setState("山东省");
    accountAddressRegisterCo.setPostalCode("250101");
    accountAddressRegisterCo.setCountry("中国");
    accountRegisterCo.setAddresses(Collections.singletonList(accountAddressRegisterCo));
    return accountRegisterCo;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void updateById() throws Exception {
    AccountUpdateByIdCmd accountUpdateByIdCmd = new AccountUpdateByIdCmd();
    AccountUpdateByIdCo accountUpdateByIdCo = new AccountUpdateByIdCo();
    accountUpdateByIdCo.setId(1L);
    accountUpdateByIdCo.setUsername("test_updated");
    accountUpdateByIdCmd.setAccountUpdateByIdCo(accountUpdateByIdCo);
    mockMvc.perform(MockMvcRequestBuilders
        .put("/account/updateById").with(csrf())
        .content(objectMapper.writeValueAsBytes(accountUpdateByIdCmd))
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void updateRoleById() throws Exception {
    AccountUpdateRoleCmd accountUpdateRoleCmd = new AccountUpdateRoleCmd();
    AccountUpdateRoleCo accountUpdateRoleCo = new AccountUpdateRoleCo();
    accountUpdateRoleCo.setId(1L);
    accountUpdateRoleCo.setRoleCodes(Collections.singletonList("test"));
    accountUpdateRoleCmd.setAccountUpdateRoleCo(accountUpdateRoleCo);
    mockMvc.perform(MockMvcRequestBuilders
        .put("/account/updateRoleById").with(csrf())
        .content(objectMapper.writeValueAsBytes(accountUpdateRoleCmd))
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void disable() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .put("/account/disable/1").with(csrf())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void resetPassword() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .put("/account/resetPassword/1").with(csrf())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void verifyPassword() throws Exception {
    AccountPasswordVerifyCmd accountPasswordVerifyCmd = new AccountPasswordVerifyCmd();
    accountPasswordVerifyCmd.setPassword("admin");
    mockMvc.perform(MockMvcRequestBuilders
        .get("/account/verifyPassword").with(csrf())
        .content(objectMapper.writeValueAsBytes(accountPasswordVerifyCmd))
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void changePassword() throws Exception {
    AccountChangePasswordCmd accountChangePasswordCmd = new AccountChangePasswordCmd();
    accountChangePasswordCmd.setNewPassword("admin1");
    accountChangePasswordCmd.setOriginalPassword("admin");
    mockMvc.perform(MockMvcRequestBuilders
        .put("/account/changePassword").with(csrf())
        .content(objectMapper.writeValueAsBytes(accountChangePasswordCmd))
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void deleteCurrent() throws Exception {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
      .setLength(Int32Value.of(4))
      .setTtl(Int64Value.of(500)).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    AccountDeleteCurrentCmd accountDeleteCurrentCmd = new AccountDeleteCurrentCmd();
    accountDeleteCurrentCmd.setCaptchaId(simpleCaptchaGeneratedGrpcCo.getId().getValue());
    accountDeleteCurrentCmd.setCaptcha(simpleCaptchaGeneratedGrpcCo.getTarget().getValue());
    mockMvc.perform(MockMvcRequestBuilders
        .delete("/account/deleteCurrent").with(csrf())
        .content(objectMapper.writeValueAsString(accountDeleteCurrentCmd).getBytes())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void archiveById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .put("/account/archiveById/1").with(csrf())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .put("/account/recoverFromArchiveById/1").with(csrf())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }
}
