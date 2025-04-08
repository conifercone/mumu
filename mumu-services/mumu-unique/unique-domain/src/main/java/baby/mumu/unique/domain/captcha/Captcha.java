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
package baby.mumu.unique.domain.captcha;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.domain.BasisDomainModel;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 验证码领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Captcha extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = -3163576648222703667L;

  /**
   * 简单验证码
   */
  private SimpleCaptcha simpleCaptcha;


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Metamodel
  public static class SimpleCaptcha {

    /**
     * 验证码id
     */
    private Long id;

    /**
     * 验证码来源值
     */
    private String source;

    /**
     * 验证码目标值
     */
    private String target;

    /**
     * 有效期
     */
    private Long ttl;

    /**
     * 验证码长度
     */
    private Integer length;
  }

}
