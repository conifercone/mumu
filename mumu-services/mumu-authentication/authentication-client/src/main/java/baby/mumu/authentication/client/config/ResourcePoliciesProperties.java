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
package baby.mumu.authentication.client.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 资源服务器配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@ConfigurationProperties("mumu.resource.policies")
public class ResourcePoliciesProperties {

  @NestedConfigurationProperty
  private List<HttpPolicy> http = new ArrayList<>();

  @NestedConfigurationProperty
  private List<Grpc> grpc = new ArrayList<>();


  @Data
  public static class HttpPolicy {

    private String httpMethod;

    private String matcher;

    private String authority;

    private List<String> anyAuthority = new ArrayList<>();

    private String role;

    private List<String> anyRole = new ArrayList<>();

    private boolean permitAll;

    private boolean denyAll;

    private boolean authenticated;
  }

  /**
   * grpc方法权限
   *
   * @since 1.0.4
   */
  @Data
  public static class Grpc {

    private String serviceFullPath;

    @NestedConfigurationProperty
    private List<GrpcPolicy> grpcPolicies = new ArrayList<>();
  }

  /**
   * grpc方法权限策略
   *
   * @since 1.0.4
   */
  @Data
  public static class GrpcPolicy {

    private String method;

    private String authority;

    private List<String> anyAuthority = new ArrayList<>();

    private String role;

    private List<String> anyRole = new ArrayList<>();

    private boolean permitAll;

    private boolean denyAll;

    private boolean authenticated;
  }
}
