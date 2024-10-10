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

import baby.mumu.authentication.client.dto.RoleAddCmd;
import baby.mumu.authentication.client.dto.RoleArchiveByIdCmd;
import baby.mumu.authentication.client.dto.RoleDeleteByIdCmd;
import baby.mumu.authentication.client.dto.RoleFindAllCmd;
import baby.mumu.authentication.client.dto.RoleRecoverFromArchiveByIdCmd;
import baby.mumu.authentication.client.dto.RoleUpdateCmd;
import baby.mumu.authentication.client.dto.co.RoleAddCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllQueryCo;
import baby.mumu.authentication.client.dto.co.RoleUpdateCo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
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
 * 角色相关web接口单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
public class RoleControllerTest {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @Autowired
  public RoleControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void add() throws Exception {
    RoleAddCmd roleAddCmd = new RoleAddCmd();
    RoleAddCo roleAddCo = new RoleAddCo();
    roleAddCo.setId(451235432L);
    roleAddCo.setName("测试角色");
    roleAddCo.setCode("test_code");
    roleAddCo.setAuthorityIds(Arrays.asList(1L, 2L));
    roleAddCmd.setRoleAddCo(roleAddCo);
    mockMvc.perform(MockMvcRequestBuilders
            .post("/role/add").with(csrf())
            .content(objectMapper.writeValueAsBytes(roleAddCmd))
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
    RoleDeleteByIdCmd roleDeleteByIdCmd = new RoleDeleteByIdCmd();
    roleDeleteByIdCmd.setId(0L);
    mockMvc.perform(MockMvcRequestBuilders
            .delete("/role/deleteById").with(csrf())
            .content(objectMapper.writeValueAsBytes(roleDeleteByIdCmd))
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
    RoleUpdateCmd roleUpdateCmd = new RoleUpdateCmd();
    RoleUpdateCo roleUpdateCo = new RoleUpdateCo();
    roleUpdateCo.setId(0L);
    roleUpdateCo.setCode("test_updated");
    roleUpdateCmd.setRoleUpdateCo(roleUpdateCo);
    mockMvc.perform(MockMvcRequestBuilders
            .put("/role/updateById").with(csrf())
            .content(objectMapper.writeValueAsBytes(roleUpdateCmd))
            .header("X-Forwarded-For", "123.123.123.123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

  @Test
  public void findAll() throws Exception {
    RoleFindAllCmd roleFindAllCmd = new RoleFindAllCmd();
    roleFindAllCmd.setPageNo(0);
    roleFindAllCmd.setPageSize(10);
    RoleFindAllQueryCo roleFindAllQueryCo = new RoleFindAllQueryCo();
    roleFindAllQueryCo.setName("管理员");
    roleFindAllCmd.setRoleFindAllQueryCo(roleFindAllQueryCo);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/role/findAll")
            .content(objectMapper.writeValueAsBytes(roleFindAllCmd))
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
    RoleArchiveByIdCmd roleArchiveByIdCmd = new RoleArchiveByIdCmd();
    roleArchiveByIdCmd.setId(0L);
    mockMvc.perform(MockMvcRequestBuilders
            .put("/role/archiveById").with(csrf())
            .content(objectMapper.writeValueAsBytes(roleArchiveByIdCmd))
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
    RoleRecoverFromArchiveByIdCmd roleRecoverFromArchiveByIdCmd = new RoleRecoverFromArchiveByIdCmd();
    roleRecoverFromArchiveByIdCmd.setId(0L);
    mockMvc.perform(MockMvcRequestBuilders
            .put("/role/recoverFromArchiveById").with(csrf())
            .content(objectMapper.writeValueAsBytes(roleRecoverFromArchiveByIdCmd))
            .header("X-Forwarded-For", "123.123.123.123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }

}
