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

import jakarta.annotation.Resource;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
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
public class AuthorityControllerTest {

  @Resource
  private MockMvc mockMvc;

  @Test
  @Transactional
  public void add() throws Exception {
    @Language("JSON") String authority = """
        {
            "authorityAddCo": {
                "id": 412354321321,
                "code": "test",
                "name": "test"
            }
        }""";
    mockMvc.perform(MockMvcRequestBuilders
            .post("/authority/add").with(csrf())
            .content(authority.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional
  public void delete() throws Exception {
    @Language("JSON") String authority = """
        {
            "authorityDeleteCo": {
                "id": 1
            }
        }""";
    mockMvc.perform(MockMvcRequestBuilders
            .delete("/authority/delete").with(csrf())
            .content(authority.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional
  public void updateById() throws Exception {
    @Language("JSON") String authority = """
        {
            "authorityUpdateCo": {
                "id": 1,
                "name": "updated"
            }
        }""";
    mockMvc.perform(MockMvcRequestBuilders
            .put("/authority/updateById").with(csrf())
            .content(authority.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional
  public void findAll() throws Exception {
    @Language("JSON") String authority = """
        {
             "authorityFindAllCo": {
                 "id": 1
             },
             "pageNo": 0,
             "pageSize": 10
         }""";
    mockMvc.perform(MockMvcRequestBuilders
            .get("/authority/findAll")
            .content(authority.getBytes())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

}
