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

package baby.mumu.extension.filters;

import baby.mumu.basis.kotlin.tools.SignatureUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties.DigitalSignature;
import baby.mumu.extension.GlobalProperties.DigitalSignature.RequestMethod;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 签名过滤器
 * <p>签名格式：时间戳+request ID+request URI+body JSON</p>
 * <p>body JSON格式：紧凑JSON，不包含多余换行和空格符</p>
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
public class SignatureFilter extends OncePerRequestFilter {

  public static final String X_SIGNATURE = "X-Signature";
  public static final String X_TIMESTAMP = "X-Timestamp";
  public static final String X_REQUEST_ID = "X-Request-ID";
  private final ExtensionProperties extensionProperties;
  private static final Logger log = LoggerFactory.getLogger(
    SignatureFilter.class);
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  public SignatureFilter(ExtensionProperties extensionProperties) {
    this.extensionProperties = extensionProperties;
  }

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request,
    @NotNull HttpServletResponse response,
    @NotNull FilterChain filterChain) throws ServletException, IOException {
    CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(
      request);
    String requestURI = request.getRequestURI();
    DigitalSignature digitalSignature = extensionProperties.getGlobal().getDigitalSignature();
    if (!isAllowed(requestURI, request.getMethod(), digitalSignature.getAllowlist())) {
      String signature = request.getHeader(X_SIGNATURE);
      String timestamp = request.getHeader(X_TIMESTAMP);
      String requestId = request.getHeader(X_REQUEST_ID);
      if (StringUtils.isNotBlank(signature) && StringUtils.isNotBlank(timestamp)) {
        try {
          if (!SignatureUtils.validateSignature(
            timestamp.concat(requestId).concat(
                StringUtils.isNotBlank(request.getQueryString()) ? requestURI.concat("?")
                  .concat(URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8))
                  : requestURI)
              .concat(cachedBodyHttpServletRequest.getBody()), signature,
            digitalSignature.getSecretKey(),
            digitalSignature.getAlgorithm())) {
            log.error(ResponseCode.DIGITAL_SIGNATURE_AUTHENTICATION_FAILED.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            ResponseWrapper.exceptionResponse(response,
              ResponseCode.DIGITAL_SIGNATURE_AUTHENTICATION_FAILED);
            return;
          }
        } catch (Exception e) {
          log.error(ResponseCode.DIGITAL_SIGNATURE_AUTHENTICATION_FAILED.getMessage());
          response.setStatus(HttpStatus.BAD_REQUEST.value());
          ResponseWrapper.exceptionResponse(response,
            ResponseCode.DIGITAL_SIGNATURE_AUTHENTICATION_FAILED);
          return;
        }
      } else {
        log.error(ResponseCode.DIGITAL_SIGNATURE_AUTHENTICATION_FAILED.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        ResponseWrapper.exceptionResponse(response,
          ResponseCode.DIGITAL_SIGNATURE_AUTHENTICATION_FAILED);
        return;
      }
    }
    // 放行
    filterChain.doFilter(cachedBodyHttpServletRequest, response);
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

  // 自定义 HttpServletRequestWrapper
  @Getter
  private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final String body;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
      super(request);
      StringBuilder jsonString = new StringBuilder();
      String line;

      try (BufferedReader reader = request.getReader()) {
        while ((line = reader.readLine()) != null) {
          jsonString.append(line);
        }
      }
      body = jsonString.toString().replaceAll("\\s+", "");
    }

    @Contract(" -> new")
    @Override
    public @NotNull BufferedReader getReader() {
      return new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))));
    }

    @Contract(" -> new")
    @Override
    public @NotNull ServletInputStream getInputStream() {
      return new ServletInputStreamWrapper(
        new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }

    // 包装 ServletInputStream
    private static class ServletInputStreamWrapper extends ServletInputStream {

      private final ByteArrayInputStream inputStream;

      public ServletInputStreamWrapper(ByteArrayInputStream inputStream) {
        this.inputStream = inputStream;
      }

      @Override
      public boolean isFinished() {
        return inputStream.available() == 0;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
        try {
          if (inputStream.available() > 0) {
            readListener.onDataAvailable();
          } else {
            readListener.onAllDataRead();
          }
        } catch (IOException e) {
          readListener.onError(e);
        }
      }

      @Override
      public int read() {
        return inputStream.read();
      }
    }
  }
}
