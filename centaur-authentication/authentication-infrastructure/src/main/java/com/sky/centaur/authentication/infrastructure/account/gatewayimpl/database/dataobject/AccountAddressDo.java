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
package com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject;

import com.sky.centaur.basis.annotations.GenerateDescription;
import com.sky.centaur.basis.dataobject.jpa.JpaBasisDataObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

/**
 * 账户地址存储对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Getter
@Setter
@Entity
@Table(name = "user_addresses")
@DynamicInsert
@GenerateDescription
@RequiredArgsConstructor
public class AccountAddressDo extends JpaBasisDataObject {

  /**
   * 唯一主键
   */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /**
   * 账户ID
   */
  @ColumnDefault("0")
  @Column(name = "user_id", nullable = false)
  private Long userId;

  /**
   * 街道地址，包含门牌号和街道信息
   */
  @Size(max = 255)
  @ColumnDefault("''")
  @Column(name = "street", nullable = false)
  private String street;

  /**
   * 城市信息
   */
  @Size(max = 100)
  @ColumnDefault("''")
  @Column(name = "city", nullable = false, length = 100)
  private String city;

  /**
   * 州或省的信息
   */
  @Size(max = 100)
  @ColumnDefault("''")
  @Column(name = "state", nullable = false, length = 100)
  private String state;

  /**
   * 邮政编码
   */
  @Size(max = 20)
  @ColumnDefault("''")
  @Column(name = "postal_code", nullable = false, length = 20)
  private String postalCode;

  /**
   * 国家信息
   */
  @Size(max = 100)
  @ColumnDefault("''")
  @Column(name = "country", nullable = false, length = 100)
  private String country;

}
