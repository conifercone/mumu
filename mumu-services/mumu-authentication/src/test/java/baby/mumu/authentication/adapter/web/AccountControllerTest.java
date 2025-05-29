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
package baby.mumu.authentication.adapter.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import baby.mumu.authentication.client.cmds.AccountChangePasswordCmd;
import baby.mumu.authentication.client.cmds.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.cmds.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd.AccountAddressRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd.AccountAvatarRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.basis.enums.AccountAvatarSourceEnum;
import baby.mumu.basis.enums.GenderEnum;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.unique.client.api.CaptchaGrpcService;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账号相关web接口单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
@TestPropertySource(properties = {
  "mumu.extension.global.digital-signature.enabled=false",
  "mumu.extension.idempotent.request-id.enabled=false"
})
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
      .setLength(4)
      .setTtl(500).build();
    SimpleCaptchaGeneratedGrpcDTO simpleCaptchaGeneratedGrpcDTO = captchaGrpcService.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    AccountRegisterCmd accountRegisterCmd = new AccountRegisterCmd();
    accountRegisterCmd.setCaptchaId(simpleCaptchaGeneratedGrpcDTO.getId());
    accountRegisterCmd.setCaptcha(simpleCaptchaGeneratedGrpcDTO.getTarget());
    accountRegisterCmd.setUsername("test1");
    accountRegisterCmd.setPassword("Test@123456");
    accountRegisterCmd.setRoleCodes(Collections.singletonList("admin"));
    AccountAvatarRegisterCmd accountAvatarRegisterCmd = new AccountAvatarRegisterCmd();
    accountAvatarRegisterCmd.setUrl(
      "https://s.gravatar.com/avatar/0d8f419ee8a9a62d9517544e647e0c99a7222cbcc3e299daff146a0fc0468b5d");
    accountAvatarRegisterCmd.setSource(AccountAvatarSourceEnum.URL);
    accountRegisterCmd.setAvatar(accountAvatarRegisterCmd);
    accountRegisterCmd.setPhone("13031723736");
    accountRegisterCmd.setGender(GenderEnum.MALE);
    accountRegisterCmd.setLanguage(LanguageEnum.ZH);
    accountRegisterCmd.setTimezone("Asia/Shanghai");
    accountRegisterCmd.setEmail("547913250@qq.com");
    accountRegisterCmd.setBirthday(LocalDate.of(1995, 8, 2));
    AccountAddressRegisterCmd accountAddressRegisterCmd = new AccountAddressRegisterCmd();
    accountAddressRegisterCmd.setStreet("历城区");
    accountAddressRegisterCmd.setCity("济南市");
    accountAddressRegisterCmd.setState("山东省");
    accountAddressRegisterCmd.setPostalCode("250000");
    accountAddressRegisterCmd.setCountry("中国");
    accountRegisterCmd.setAddresses(Collections.singletonList(accountAddressRegisterCmd));
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

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void updateById() throws Exception {
    AccountUpdateByIdCmd accountUpdateByIdCmd = new AccountUpdateByIdCmd();
    accountUpdateByIdCmd.setId(1L);
    accountUpdateByIdCmd.setUsername("test_updated");
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
    accountUpdateRoleCmd.setId(1L);
    accountUpdateRoleCmd.setRoleCodes(Collections.singletonList("test"));
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
    accountPasswordVerifyCmd.setPassword("Admin@5211314");
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
    accountChangePasswordCmd.setNewPassword("Admin@5211314");
    accountChangePasswordCmd.setOriginalPassword("Admin@5211314");
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
      .setLength(4)
      .setTtl(500).build();
    SimpleCaptchaGeneratedGrpcDTO simpleCaptchaGeneratedGrpcDTO = captchaGrpcService.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    AccountDeleteCurrentCmd accountDeleteCurrentCmd = new AccountDeleteCurrentCmd();
    accountDeleteCurrentCmd.setCaptchaId(simpleCaptchaGeneratedGrpcDTO.getId());
    accountDeleteCurrentCmd.setCaptcha(simpleCaptchaGeneratedGrpcDTO.getTarget());
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
