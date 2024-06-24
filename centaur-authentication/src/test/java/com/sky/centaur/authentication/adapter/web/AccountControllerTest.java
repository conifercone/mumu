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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.intellij.lang.annotations.Language;
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
 * @author kaiyu.shan
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
public class AccountControllerTest {

  private final MockMvc mockMvc;

  @Autowired
  public AccountControllerTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void register() throws Exception {
    @Language("JSON") String userInfo = """
        {
             "accountRegisterCo": {
                 "id": 31241232131,
                 "username": "test1",
                 "password": "test1",
                 "enabled": true,
                 "roleCode": "admin",
                 "avatarUrl": "https://github.com/users/conifercone",
                 "phone": "13031723736",
                 "sex": "MALE",
                 "language": "ZH",
                 "timezone": "UTF+8"
             }
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .post("/account/register").with(csrf())
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void updateById() throws Exception {
    @Language("JSON") String userInfo = """
        {
             "accountUpdateByIdCo": {
                 "id": 1,
                 "sex": "MALE"
             }
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/account/updateById").with(csrf())
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void updateRoleById() throws Exception {
    @Language("JSON") String userInfo = """
        {
             "accountUpdateRoleCo": {
                 "id": 1,
                 "roleCode": "test"
             }
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/account/updateRoleById").with(csrf())
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void disable() throws Exception {
    @Language("JSON") String userInfo = """
        {
             "accountDisableCo": {
                 "id": 1
             }
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/account/disable").with(csrf())
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void resetPassword() throws Exception {
    @Language("JSON") String userInfo = """
        {
             "accountResetPasswordCo": {
                 "id": 1
             }
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/account/resetPassword").with(csrf())
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void verifyPassword() throws Exception {
    @Language("JSON") String userInfo = """
        {
             "password": "admin"
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .get("/account/verifyPassword").with(csrf())
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void changePassword() throws Exception {
    @Language("JSON") String userInfo = """
        {
             "originalPassword": "admin",
             "newPassword": "admin1"
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/account/changePassword").with(csrf())
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }
}
