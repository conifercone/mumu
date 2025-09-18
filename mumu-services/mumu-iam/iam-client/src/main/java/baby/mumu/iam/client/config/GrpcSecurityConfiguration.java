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

import baby.mumu.basis.constants.CommonConstants;
import io.grpc.MethodDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.util.Assert;

/**
 * grpc server端权限配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Configuration
public class GrpcSecurityConfiguration {

  private static final String GET_METHOD_TEMPLATE = "get%sMethod";
  private final ResourcePoliciesProperties props;

  private static final String AUTHORITY_PREFIX =
    CommonConstants.AUTHORITY_PREFIX;
  private static final String ROLE_PREFIX = CommonConstants.ROLE_PREFIX;

  @Autowired
  public GrpcSecurityConfiguration(ResourcePoliciesProperties props) {
    this.props = props;
  }

  @Bean
  @GlobalServerInterceptor
  AuthenticationProcessInterceptor authenticationProcessInterceptor(GrpcSecurity grpc)
    throws Exception {
    // 1) 启用 OAuth2 资源服务器（会自动挂上 Bearer 提取器）
    grpc.oauth2ResourceServer(cfg -> cfg.jwt(_ -> {
    }));

    // 2) 基于方法路径的授权映射
    grpc.authorizeRequests(r -> {
      // 放开 gRPC 内置服务
      r.methods("grpc.*/*").permitAll();

      if (CollectionUtils.isNotEmpty(props.getGrpc())) {
        for (var svc : props.getGrpc()) {
          if (CollectionUtils.isEmpty(svc.getGrpcPolicies())) {
            continue;
          }

          for (var p : svc.getGrpcPolicies()) {
            String full;
            try {
              full = GrpcSecurityConfiguration.resolveFullMethodName(svc.getServiceFullPath(),
                p.getMethod());
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
            var m = r.methods(full);

            if (StringUtils.isNotBlank(p.getRole())) {
              m.hasAuthority(roleToAuthority(p.getRole()));
            } else if (CollectionUtils.isNotEmpty(p.getAnyRole())) {
              m.hasAnyAuthority(
                p.getAnyRole().stream()
                  .filter(StringUtils::isNotBlank)
                  .map(this::roleToAuthority)
                  .distinct()
                  .toArray(String[]::new)
              );
            } else if (StringUtils.isNotBlank(p.getAuthority())) {
              Assert.isTrue(
                !p.getAuthority().startsWith(GrpcSecurityConfiguration.AUTHORITY_PREFIX),
                "Permission cannot start with " + GrpcSecurityConfiguration.AUTHORITY_PREFIX);
              m.hasAuthority(GrpcSecurityConfiguration.AUTHORITY_PREFIX + p.getAuthority());
            } else if (CollectionUtils.isNotEmpty(p.getAnyAuthority())) {
              GrpcSecurityConfiguration.validateAuthorities(p.getAnyAuthority());
              Set<String> granted = p.getAnyAuthority().stream()
                .distinct()
                .map(a -> GrpcSecurityConfiguration.AUTHORITY_PREFIX + a)
                .collect(Collectors.toSet());
              m.hasAnyAuthority(granted.toArray(String[]::new));
            } else if (p.isPermitAll()) {
              m.permitAll();
            } else if (p.isDenyAll()) {
              m.denyAll();
            } else if (p.isAuthenticated()) {
              m.authenticated();
            } else {
              m.denyAll(); // 未声明一律拒绝
            }
          }
        }
      }

      // 兜底
      r.allRequests().denyAll();
    });

    return grpc.build();
  }

  private String roleToAuthority(String role) {
    String r = StringUtils.trim(role);
    if (StringUtils.isBlank(r)) {
      throw new IllegalArgumentException("role must not be blank");
    }
    return r.startsWith(GrpcSecurityConfiguration.ROLE_PREFIX) ? r :
      GrpcSecurityConfiguration.ROLE_PREFIX + r;
  }

  private static void validateAuthorities(List<String> anyAuthority) {
    anyAuthority.stream()
      .filter(a -> StringUtils.isBlank(a) || a.startsWith(
        GrpcSecurityConfiguration.AUTHORITY_PREFIX))
      .findAny()
      .ifPresent(_ -> {
        throw new IllegalArgumentException(
          "Permission cannot be empty and cannot start with "
            + GrpcSecurityConfiguration.AUTHORITY_PREFIX);
      });
  }

  private static String resolveFullMethodName(String serviceFullPath, String methodName)
    throws Exception {
    Class<?> clazz = Class.forName(serviceFullPath);
    Method m = clazz.getDeclaredMethod(String.format(GrpcSecurityConfiguration.GET_METHOD_TEMPLATE,
      GrpcSecurityConfiguration.capitalize(methodName)));
    MethodDescriptor<?, ?> md = (MethodDescriptor<?, ?>) m.invoke(null);
    return md.getFullMethodName(); // e.g. "com.foo.UserService/GetUser"
  }

  private static String capitalize(String s) {
    if (StringUtils.isBlank(s)) {
      return s;
    }
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

}
