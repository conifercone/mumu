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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.document.po;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.po.jpa.JpaDocumentBasisDefaultPersistentObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 账号地址存储对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Document("mumu-account-addresses")
@Metamodel
public class AccountAddressDocumentPO extends JpaDocumentBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = -8437677767812120031L;

  /**
   * 唯一主键
   */
  @Id
  @NotBlank
  private String id;

  /**
   * 账号ID
   */
  @NotNull
  @Indexed(background = true)
  private Long accountId;

  /**
   * 街道地址，包含门牌号和街道信息
   */
  @Size(max = 255)
  @Indexed(background = true)
  private String street;

  /**
   * 城市信息
   */
  @Size(max = 100)
  @Indexed(background = true)
  private String city;

  /**
   * 州或省的信息
   */
  @Size(max = 100)
  @Indexed(background = true)
  private String state;

  /**
   * 邮政编码
   */
  @Size(max = 20)
  @Indexed(background = true)
  private String postalCode;

  /**
   * 国家信息
   */
  @Size(max = 100)
  @Indexed(background = true)
  private String country;

  /**
   * 定位（WGS84坐标系）
   */
  @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
  private GeoJsonPoint location;

  /**
   * 是否为默认地址
   */
  @Indexed(background = true)
  private boolean defaultAddress;

  @Version
  private Long version;
}
