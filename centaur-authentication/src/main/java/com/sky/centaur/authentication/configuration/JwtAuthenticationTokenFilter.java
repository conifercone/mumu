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

package com.sky.centaur.authentication.configuration;

import com.sky.centaur.basis.enums.TokenClaimsEnum;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.response.ResultResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * jwt token认证拦截器
 *
 * @author 单开宇
 * @since 2024-02-27
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_START = "Bearer ";
  UserDetailsService userDetailsService;
  JwtDecoder jwtDecoder;
  private static final Logger LOGGER = LoggerFactory.getLogger(
      JwtAuthenticationTokenFilter.class);

  public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService,
      JwtDecoder jwtDecoder) {
    this.userDetailsService = userDetailsService;
    this.jwtDecoder = jwtDecoder;
  }

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader(TOKEN_HEADER);
    // 存在token
    if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_START)) {
      String authToken = authHeader.substring(TOKEN_START.length());
      Jwt jwt;
      try {
        jwt = jwtDecoder.decode(authToken);
      } catch (JwtException e) {
        LOGGER.error(ResultCode.INVALID_TOKEN.getResultCode());
        response.setStatus(Integer.parseInt(ResultCode.UNAUTHORIZED.getResultCode()));
        ResultResponse.exceptionResponse(response, ResultCode.INVALID_TOKEN);
        return;
      }
      String accountName = jwt.getClaimAsString(TokenClaimsEnum.ACCOUNT_NAME.name());
      // 存在token 但是用户名未登录
      if (accountName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // 登录
        UserDetails userDetails = userDetailsService.loadUserByUsername(accountName);
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        // 重新设置回用户对象
        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }
    // 放行
    filterChain.doFilter(request, response);
  }
}
