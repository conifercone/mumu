package com.sky.centaur.authentication.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * 鉴权服务全局配置信息
 *
 * @author 单开宇
 * @since 2024-01-19
 */
@Data
@Component
@ConfigurationProperties("auth")
public class AuthenticationProperties {

  @NestedConfigurationProperty
  private Security security = new Security();

  @Data
  public static class Security {

    private List<String> excludeUrls = new ArrayList<>();
  }
}
