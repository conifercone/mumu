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

package baby.mumu.iam.domain.account;

import baby.mumu.basis.domain.BasisDomainModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.geo.Point;

/**
 * 账号地址领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Data
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountAddress extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = 1106704309433693382L;

  /**
   * 唯一主键
   */
  private String id;

  /**
   * 账号ID
   */
  private Long accountId;

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

  /**
   * 定位
   */
  private Point location;

  /**
   * 是否为默认地址
   */
  private boolean defaultAddress;

  private Long version;
}

