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

import lombok.Data;

/**
 * 根据国家ID获取省或州客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Data
public class CountryGetStatesByCountryIdCo {

  /**
   * 唯一标识
   */
  private Long id;

  /**
   * 名称
   */
  private String name;

  /**
   * 纬度
   */
  private String latitude;

  /**
   * 精度
   */
  private String longitude;
}
