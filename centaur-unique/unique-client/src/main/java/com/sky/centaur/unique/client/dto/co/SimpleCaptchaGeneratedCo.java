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

package com.sky.centaur.unique.client.dto.co;

import com.sky.centaur.basis.annotations.GenerateDescription;
import com.sky.centaur.basis.client.dto.co.BaseClientObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 简单验证码生成客户端对象
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GenerateDescription
public class SimpleCaptchaGeneratedCo extends BaseClientObject {

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
