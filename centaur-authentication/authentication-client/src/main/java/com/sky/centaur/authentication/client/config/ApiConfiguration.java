package com.sky.centaur.authentication.client.config;

import com.sky.centaur.authentication.client.api.AccountGrpcService;
import com.sky.centaur.authentication.client.api.AuthorityGrpcService;
import com.sky.centaur.authentication.client.api.RoleGrpcService;
import com.sky.centaur.authentication.client.api.TokenGrpcService;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api配置类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
public class ApiConfiguration {

  @Bean
  public TokenGrpcService tokenGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new TokenGrpcService(consulDiscoveryClient);
  }

  @Bean
  public AccountGrpcService accountGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new AccountGrpcService(consulDiscoveryClient);
  }

  @Bean
  public AuthorityGrpcService authorityGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new AuthorityGrpcService(consulDiscoveryClient);
  }

  @Bean
  public RoleGrpcService roleGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new RoleGrpcService(consulDiscoveryClient);
  }
}
