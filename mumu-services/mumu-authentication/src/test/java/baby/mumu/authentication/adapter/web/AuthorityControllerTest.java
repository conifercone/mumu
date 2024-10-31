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

import baby.mumu.authentication.client.dto.AuthorityAddCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityUpdateCmd;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AuthorityControllerTest {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @Autowired
  public AuthorityControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void add() throws Exception {
    AuthorityAddCmd authorityAddCmd = new AuthorityAddCmd();
    authorityAddCmd.setId(412354321321L);
    authorityAddCmd.setCode("test_code");
    authorityAddCmd.setName("test_name");
    mockMvc.perform(MockMvcRequestBuilders
        .post("/authority/add").with(csrf())
        .content(objectMapper.writeValueAsBytes(authorityAddCmd))
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void deleteById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .delete("/authority/deleteById/3").with(csrf())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().is5xxServerError())
      .andDo(print());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void updateById() throws Exception {
    AuthorityUpdateCmd authorityUpdateCmd = new AuthorityUpdateCmd();
    authorityUpdateCmd.setId(3L);
    authorityUpdateCmd.setCode("test_updated");
    mockMvc.perform(MockMvcRequestBuilders
        .put("/authority/updateById").with(csrf())
        .content(objectMapper.writeValueAsBytes(authorityUpdateCmd))
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  public void findAll() throws Exception {
    AuthorityFindAllCmd authorityFindAllCmd = new AuthorityFindAllCmd();
    authorityFindAllCmd.setCurrent(0);
    authorityFindAllCmd.setPageSize(10);
    authorityFindAllCmd.setId(1L);
    mockMvc.perform(MockMvcRequestBuilders
        .get("/authority/findAll")
        .content(objectMapper.writeValueAsBytes(authorityFindAllCmd))
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
        .put("/authority/archiveById/3").with(csrf())
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
        .put("/authority/recoverFromArchiveById/3").with(csrf())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }
}
