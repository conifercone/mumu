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
package baby.mumu.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 自动获取token
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
public class AuthenticationRequired {

  public Optional<String> getToken() {
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();
    String url = "http://localhost:9080/api/mumu/authentication/oauth2/token";
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("username", "admin");
    form.add("password", "admin");
    form.add("scope", "message.read message.write openid");
    form.add("grant_type", "authorization_password");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.add("Authorization", "Basic Y2VudGF1ci1jbGllbnQ6Y2VudGF1cg==");
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
        String.class);
    if (response.getStatusCode().value() == HttpStatus.SC_OK) {
      try {
        String contentAsString = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(contentAsString);
        JsonNode accessToken = jsonNode.get("access_token");
        return Optional.ofNullable(accessToken.textValue());
      } catch (JsonProcessingException e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }
}
