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

package baby.mumu.genix.client.cmds;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 简单验证码生成指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Data
public class CaptchaCodeGeneratedCmd {

  /**
   * 有效期
   */
  @Size(max = 3600)
  private Long ttl;

  /**
   * 验证码长度
   */
  @Size(min = 1, max = 10)
  private Integer length;
}
