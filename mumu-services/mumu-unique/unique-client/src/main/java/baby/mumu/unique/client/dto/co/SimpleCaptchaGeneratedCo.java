/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.unique.client.dto.co;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.client.dto.co.BaseClientObject;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 简单验证码生成客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Metamodel
public class SimpleCaptchaGeneratedCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 2364403958860562068L;

  /**
   * 验证码id
   */
  private Long id;

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
