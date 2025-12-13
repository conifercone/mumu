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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import baby.mumu.basis.constants.SpringBootConstants;
import baby.mumu.iam.IAMApplicationMetamodel;
import baby.mumu.iam.client.cmds.PermissionAddCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionUpdateCmd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

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
  "mumu.extension.idempotent.request-id.enabled=false",
  SpringBootConstants.APPLICATION_TITLE + "=" + IAMApplicationMetamodel.PROJECT_NAME,
  SpringBootConstants.APPLICATION_FORMATTED_VERSION + "="
    + IAMApplicationMetamodel.FORMATTED_PROJECT_VERSION,
})
public class PermissionControllerTest {

  private final MockMvc mockMvc;
  private final JsonMapper jsonMapper;

  @Autowired
  public PermissionControllerTest(MockMvc mockMvc, JsonMapper jsonMapper) {
    this.mockMvc = mockMvc;
    this.jsonMapper = jsonMapper;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void add() throws Exception {
    PermissionAddCmd permissionAddCmd = new PermissionAddCmd();
    permissionAddCmd.setCode("test_code");
    permissionAddCmd.setName("test_name");
    mockMvc.perform(MockMvcRequestBuilders
        .post("/permission/add").with(csrf())
        .content(jsonMapper.writeValueAsBytes(permissionAddCmd))
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
        .delete("/permission/deleteById/3").with(csrf())
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
    PermissionUpdateCmd permissionUpdateCmd = new PermissionUpdateCmd();
    permissionUpdateCmd.setId(3L);
    permissionUpdateCmd.setCode("test_updated");
    mockMvc.perform(MockMvcRequestBuilders
        .put("/permission/updateById").with(csrf())
        .content(jsonMapper.writeValueAsBytes(permissionUpdateCmd))
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }

  @Test
  public void findAll() throws Exception {
    PermissionFindAllCmd permissionFindAllCmd = new PermissionFindAllCmd();
    permissionFindAllCmd.setCurrent(0);
    permissionFindAllCmd.setPageSize(10);
    permissionFindAllCmd.setId(1L);
    mockMvc.perform(MockMvcRequestBuilders
        .get("/permission/findAll")
        .content(jsonMapper.writeValueAsBytes(permissionFindAllCmd))
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
        .put("/permission/archiveById/3").with(csrf())
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
        .put("/permission/recoverFromArchiveById/3").with(csrf())
        .header("X-Forwarded-For", "123.123.123.123")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andDo(print());
  }
}
