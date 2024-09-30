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
package baby.mumu.authentication.client.config;

import baby.mumu.basis.response.ResultCode;
import baby.mumu.basis.response.ResultResponse;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 资源服务器认证失败处理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class ResourceServerAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      ResourceServerAuthenticationEntryPoint.class);

  @Override
  public void commence(jakarta.servlet.http.HttpServletRequest request,
      jakarta.servlet.http.@NotNull HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    LOGGER.error(ResultCode.INVALID_TOKEN.getResultMsg());
    response.setStatus(Integer.parseInt(ResultCode.UNAUTHORIZED.getResultCode()));
    ResultResponse.exceptionResponse(response, ResultCode.INVALID_TOKEN);
  }
}
