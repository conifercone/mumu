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
package com.sky.centaur.authentication.domain.account;

import com.sky.centaur.basis.annotations.GenerateDescription;
import com.sky.centaur.basis.domain.BasisDomainModel;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 账户地址领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@GenerateDescription
public class AccountAddress extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = 1106704309433693382L;

  /**
   * 唯一主键
   */
  private Long id;

  /**
   * 账户ID
   */
  private Long userId;

  /**
   * 街道地址，包含门牌号和街道信息
   */
  @Size(max = 255)
  private String street;

  /**
   * 城市信息
   */
  @Size(max = 100)
  private String city;

  /**
   * 州或省的信息
   */
  @Size(max = 100)
  private String state;

  /**
   * 邮政编码
   */
  @Size(max = 20)
  private String postalCode;

  /**
   * 国家信息
   */
  @Size(max = 100)
  private String country;
}
