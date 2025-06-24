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

package baby.mumu.extension.authentication;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 认证相关配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Data
public class AuthenticationProperties {

  /**
   * 初始密码
   */
  private String initialPassword = "3c38019#0e5c34f@3Bc97cae24aC6062";

  @NestedConfigurationProperty
  private Rsa rsa = new Rsa();


  @Data
  public static class Rsa {

    /**
     * 自动生成
     */
    private boolean automaticGenerated = true;

    /**
     * 密钥地址
     */
    private String jksKeyPath;
    /**
     * 密钥密码
     */
    private String jksKeyPassword;

    /**
     * 密钥对
     */
    private String jksKeyPair = "mumu";
  }
}
