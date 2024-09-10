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
package baby.mumu.authentication.configuration;

import baby.mumu.basis.enums.OAuth2Enum;
import baby.mumu.basis.response.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * 密码模式转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class PasswordGrantAuthenticationConverter implements AuthenticationConverter {

  @Nullable
  @Override
  public Authentication convert(@NotNull HttpServletRequest request) {
    // grant_type (REQUIRED)
    String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
    if (!OAuth2Enum.GRANT_TYPE_PASSWORD.getName().equals(grantType)) {
      return null;
    }
    Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
    //从request中提取请求参数，然后存入MultiValueMap<String, String>
    MultiValueMap<String, String> parameters = getParameters(request);
    // username (REQUIRED)
    String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
    if (StringUtils.isBlank(username) ||
        parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
      ResultCode accountNameCannotBeEmpty = ResultCode.ACCOUNT_NAME_CANNOT_BE_EMPTY;
      throw new OAuth2AuthenticationException(
          new OAuth2Error(accountNameCannotBeEmpty.getResultCode(),
              accountNameCannotBeEmpty.getResultMsg(), StringUtils.EMPTY));
    }
    String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
    if (StringUtils.isBlank(password) ||
        parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
      ResultCode accountPasswordCannotBeEmpty = ResultCode.ACCOUNT_PASSWORD_CANNOT_BE_EMPTY;
      throw new OAuth2AuthenticationException(
          new OAuth2Error(accountPasswordCannotBeEmpty.getResultCode(),
              accountPasswordCannotBeEmpty.getResultMsg(), StringUtils.EMPTY));
    }
    //收集要传入PasswordGrantAuthenticationToken构造方法的参数，
    //该参数接下来在PasswordGrantAuthenticationProvider中使用
    Map<String, Object> additionalParameters = new HashMap<>();
    //遍历从request中提取的参数，排除掉grant_type、client_id、code等字段参数，其他参数收集到additionalParameters中
    parameters.forEach((key, value) -> {
      if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
          !key.equals(OAuth2ParameterNames.CLIENT_ID) &&
          !key.equals(OAuth2ParameterNames.CODE)) {
        additionalParameters.put(key, value.getFirst());
      }
    });

    //返回自定义的PasswordGrantAuthenticationToken对象
    return new PasswordGrantAuthenticationToken(clientPrincipal, additionalParameters);
  }

  /**
   * 从request中提取请求参数，然后存入MultiValueMap<String, String>
   *
   * @param request 请求
   * @return 请求参数
   */
  private static @NotNull MultiValueMap<String, String> getParameters(
      @NotNull HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
    parameterMap.forEach((key, values) -> {
      for (String value : values) {
        parameters.add(key, value);
      }
    });
    return parameters;
  }

}
