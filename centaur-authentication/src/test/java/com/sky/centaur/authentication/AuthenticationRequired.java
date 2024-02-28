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

package com.sky.centaur.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 需要认证的单元测试
 *
 * @author 单开宇
 * @since 2024-02-28
 */
public class AuthenticationRequired {

  public @Nullable String getToken(@NotNull MockMvc mockMvc) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    //noinspection SpellCheckingInspection
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
            .post("/oauth2/token")
            .param("username", "sky")
            .param("password", "yxt5211314")
            .param("scope", "message.read message.write openid")
            .param("grant_type", "authorization_password")
            .header("Authorization", "Basic Y2VudGF1ci1jbGllbnQ6Y2VudGF1cg==")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    if (response.getStatus() == HttpStatus.SC_OK) {
      String contentAsString = response.getContentAsString();
      JsonNode jsonNode = objectMapper.readTree(contentAsString);
      JsonNode accessToken = jsonNode.get("access_token");
      return accessToken.textValue();
    }
    return null;
  }
}
