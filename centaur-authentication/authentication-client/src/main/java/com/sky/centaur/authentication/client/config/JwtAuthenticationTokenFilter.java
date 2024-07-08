/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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

package com.sky.centaur.authentication.client.config;

import com.sky.centaur.authentication.client.api.TokenGrpcService;
import com.sky.centaur.authentication.client.api.grpc.TokenValidityGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.TokenValidityGrpcCo;
import com.sky.centaur.basis.enums.TokenClaimsEnum;
import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.response.ResultResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * jwt token认证拦截器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_START = "Bearer ";
  JwtDecoder jwtDecoder;
  TokenGrpcService tokenGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(
      JwtAuthenticationTokenFilter.class);

  public JwtAuthenticationTokenFilter(JwtDecoder jwtDecoder, TokenGrpcService tokenGrpcService) {
    this.jwtDecoder = jwtDecoder;
    this.tokenGrpcService = tokenGrpcService;
  }

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain) throws ServletException, IOException {
    CentaurHttpServletRequestWrapper centaurHttpServletRequestWrapper = new CentaurHttpServletRequestWrapper(
        request);
    String authHeader = centaurHttpServletRequestWrapper.getHeader(TOKEN_HEADER);
    SecurityContextUtil.getLoginAccountLanguage()
        .ifPresent(languageEnum -> centaurHttpServletRequestWrapper.setLocale(
            Locale.of(languageEnum.name())));
    // 存在token
    if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_START)) {
      String authToken = authHeader.substring(TOKEN_START.length());
      // 判断redis中是否存在token
      if (!tokenGrpcService.validity(TokenValidityGrpcCmd.newBuilder().setTokenValidityCo(
          TokenValidityGrpcCo.newBuilder().setToken(authToken).build()).build()).getValidity()) {
        LOGGER.error(ResultCode.INVALID_TOKEN.getResultMsg());
        response.setStatus(Integer.parseInt(ResultCode.UNAUTHORIZED.getResultCode()));
        ResultResponse.exceptionResponse(response, ResultCode.INVALID_TOKEN);
        return;
      }
      // 判断token是否合法
      Jwt jwt;
      try {
        jwt = jwtDecoder.decode(authToken);
      } catch (JwtException e) {
        LOGGER.error(ResultCode.INVALID_TOKEN.getResultMsg());
        response.setStatus(Integer.parseInt(ResultCode.UNAUTHORIZED.getResultCode()));
        ResultResponse.exceptionResponse(response, ResultCode.INVALID_TOKEN);
        return;
      }
      List<String> authorities = jwt.getClaimAsStringList(TokenClaimsEnum.AUTHORITIES.name());
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        JwtAuthenticationToken authenticationToken =
            new JwtAuthenticationToken(jwt, Optional.ofNullable(authorities).map(authoritySet ->
                    authoritySet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
                .orElse(null));
        // 重新设置回用户对象
        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(centaurHttpServletRequestWrapper));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextUtil.getLoginAccountLanguage()
            .ifPresent(languageEnum -> centaurHttpServletRequestWrapper.setLocale(
                Locale.of(languageEnum.name())));
      }
    }
    // 放行
    filterChain.doFilter(centaurHttpServletRequestWrapper, response);
  }
}
