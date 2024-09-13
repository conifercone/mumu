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
package baby.mumu.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 自动获取token
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class AuthenticationRequired {

  public Optional<String> getToken(@NotNull MockMvc mockMvc) {
    ObjectMapper objectMapper = new ObjectMapper();
    MvcResult mvcResult;
    try {
      byte[] encodedBytes = Base64.encodeBase64(
          String.format("%s:%s", "mumu-client", "mumu").getBytes(
              StandardCharsets.UTF_8));
      mvcResult = mockMvc.perform(MockMvcRequestBuilders
              .post("/oauth2/token")
              .param("username", "admin")
              .param("password", "admin")
              .param("scope", "message.read message.write openid")
              .param("grant_type", "authorization_password")
              .header("Authorization",
                  "Basic ".concat(new String(encodedBytes, StandardCharsets.UTF_8)))
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
          )
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn();
    } catch (Exception e) {
      return Optional.empty();
    }
    MockHttpServletResponse response = mvcResult.getResponse();
    if (response.getStatus() == HttpStatus.SC_OK) {
      try {
        String contentAsString = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(contentAsString);
        JsonNode accessToken = jsonNode.get("access_token");
        return Optional.ofNullable(accessToken.textValue());
      } catch (UnsupportedEncodingException | JsonProcessingException e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }
}
