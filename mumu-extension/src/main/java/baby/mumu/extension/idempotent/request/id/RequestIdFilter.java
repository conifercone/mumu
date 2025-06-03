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

package baby.mumu.extension.idempotent.request.id;

import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.idempotent.IdempotentProperties.RequestMethod;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * request id 过滤器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
public class RequestIdFilter extends OncePerRequestFilter {

  private final RequestIdIdempotentProcessor requestIdIdempotentProcessor;
  private static final Logger log = LoggerFactory.getLogger(
    RequestIdFilter.class);
  private final ExtensionProperties extensionProperties;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  public RequestIdFilter(RequestIdIdempotentProcessor requestIdIdempotentProcessor,
    ExtensionProperties extensionProperties) {
    this.requestIdIdempotentProcessor = requestIdIdempotentProcessor;
    this.extensionProperties = extensionProperties;
  }

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request,
    @NotNull HttpServletResponse response,
    @NotNull FilterChain filterChain) throws ServletException, IOException {
    String REQUEST_ID_HEADER = "X-Request-ID";
    String requestIdHeaderValue = request.getHeader(REQUEST_ID_HEADER);
    if ((!isAllowed(request.getRequestURI(), request.getMethod(),
      extensionProperties.getIdempotent().getAllowlist())) && (
      StringUtils.isBlank(requestIdHeaderValue) || requestIdIdempotentProcessor.processed(
        requestIdHeaderValue))) {
      log.error(ResponseCode.REQUEST_HAS_BEEN_PROCESSED.getMessage());
      response.setStatus(ResponseCode.REQUEST_HAS_BEEN_PROCESSED.getStatus());
      ResponseWrapper.exceptionResponse(response, ResponseCode.REQUEST_HAS_BEEN_PROCESSED);
      return;
    }
    requestIdIdempotentProcessor.process(requestIdHeaderValue);
    // 放行
    filterChain.doFilter(request, response);
  }

  /**
   * 检查请求路径和方法是否在白名单中
   */
  public boolean isAllowed(String requestUrl, String requestMethod,
    @NotNull List<RequestMethod> allowlist) {
    for (RequestMethod allowedMethod : allowlist) {
      if (allowedMethod.getMethod().equalsIgnoreCase(requestMethod)
        && pathMatcher.match(allowedMethod.getUrl(), requestUrl)) {
        return true;
      }
    }
    return false;
  }
}
