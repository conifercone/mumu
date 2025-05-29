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
package baby.mumu.unique.client.dto;

import java.util.List;
import lombok.Data;

/**
 * 获取所有国家数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Data
public class CountryStateCityGetAllDTO {

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

  /**
   * emoji
   */
  private String emoji;

  /**
   * 本地化名称
   */
  private String nativeName;

  /**
   * 省份或州信息
   */
  private List<CountryStateCityGetAllStateCo> states;

  @Data
  public static class CountryStateCityGetAllStateCo {

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

    /**
     * 城市信息
     */
    private List<CountryStateCityGetAllCityCo> cities;
  }

  @Data
  public static class CountryStateCityGetAllCityCo {

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
}
