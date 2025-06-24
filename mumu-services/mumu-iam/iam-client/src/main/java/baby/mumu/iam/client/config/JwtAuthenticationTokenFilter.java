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

package baby.mumu.iam.client.config;

import baby.mumu.basis.enums.TokenClaimsEnum;
import baby.mumu.basis.filters.TraceIdFilter;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.iam.client.api.TokenGrpcService;
import baby.mumu.iam.client.api.grpc.TokenValidityGrpcCmd;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * jwt token认证拦截器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private static final String TOKEN_START = "Bearer ";
  JwtDecoder jwtDecoder;
  TokenGrpcService tokenGrpcService;
  private static final Logger log = LoggerFactory.getLogger(
    JwtAuthenticationTokenFilter.class);
  Tracer tracer;

  public JwtAuthenticationTokenFilter(JwtDecoder jwtDecoder, TokenGrpcService tokenGrpcService,
    Tracer tracer) {
    this.jwtDecoder = jwtDecoder;
    this.tokenGrpcService = tokenGrpcService;
    this.tracer = tracer;
  }

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request,
    @NotNull HttpServletResponse response,
    @NotNull FilterChain filterChain) throws ServletException, IOException {
    MuMuHttpServletRequestWrapper mumuHttpServletRequestWrapper = new MuMuHttpServletRequestWrapper(
      request);
    String authHeader = mumuHttpServletRequestWrapper.getHeader(HttpHeaders.AUTHORIZATION);
    SecurityContextUtils.getLoginAccountLanguage()
      .ifPresent(languageEnum -> mumuHttpServletRequestWrapper.setLocale(
        Locale.of(languageEnum.getCode())));
    // 存在token
    if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith(
      JwtAuthenticationTokenFilter.TOKEN_START)) {
      String authToken = authHeader.substring(JwtAuthenticationTokenFilter.TOKEN_START.length());
      // 判断redis中是否存在token
      if (!tokenGrpcService.validity(TokenValidityGrpcCmd.newBuilder().setToken(authToken).build())
        .getValidity()) {
        try {
          traceId();
          JwtAuthenticationTokenFilter.log.error(ResponseCode.INVALID_TOKEN.getMessage());
          response.setStatus(ResponseCode.UNAUTHORIZED.getStatus());
          ResponseWrapper.exceptionResponse(response, ResponseCode.INVALID_TOKEN);
        } finally {
          TraceIdFilter.removeTraceId();
        }
        return;
      }
      // 判断token是否合法
      Jwt jwt;
      try {
        jwt = jwtDecoder.decode(authToken);
      } catch (JwtException e) {
        traceId();
        JwtAuthenticationTokenFilter.log.error(ResponseCode.INVALID_TOKEN.getMessage());
        response.setStatus(ResponseCode.UNAUTHORIZED.getStatus());
        ResponseWrapper.exceptionResponse(response, ResponseCode.INVALID_TOKEN);
        return;
      } finally {
        TraceIdFilter.removeTraceId();
      }
      List<String> authorities = jwt.getClaimAsStringList(
        TokenClaimsEnum.AUTHORITIES.getClaimName());
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        JwtAuthenticationToken authenticationToken =
          new JwtAuthenticationToken(jwt, Optional.ofNullable(authorities).map(authoritySet ->
              authoritySet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
            .orElse(new HashSet<>()));
        // 重新设置回用户对象
        authenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(mumuHttpServletRequestWrapper));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextUtils.getLoginAccountLanguage()
          .ifPresent(languageEnum -> mumuHttpServletRequestWrapper.setLocale(
            Locale.of(languageEnum.getCode())));
      }
    }
    // 放行
    filterChain.doFilter(mumuHttpServletRequestWrapper, response);
  }

  private void traceId() {
    Optional.ofNullable(tracer).map(Tracer::currentSpan).map(Span::context)
      .ifPresent(traceContext -> TraceIdFilter.setTraceId(traceContext.traceId()));
  }
}
