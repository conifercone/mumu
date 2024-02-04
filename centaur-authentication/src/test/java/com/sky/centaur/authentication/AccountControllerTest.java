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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import jakarta.annotation.Resource;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户相关web接口单元测试
 *
 * @author 单开宇
 * @since 2024-01-12
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AccountControllerTest {

  @Resource
  private MockMvc mockMvc;

  @Test
  @Transactional
  @Rollback()
  public void register() throws Exception {
    @Language("JSON") String userInfo = """
        {
            "accountRegisterCo": {
                "id": 6234234323143455656756,
                "username": "yxt",
                "password": "yxt5211314",
                "authorities": []
            }
        }""";
    mockMvc.perform(MockMvcRequestBuilders
            .post("/account/register")
            .content(userInfo.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

}