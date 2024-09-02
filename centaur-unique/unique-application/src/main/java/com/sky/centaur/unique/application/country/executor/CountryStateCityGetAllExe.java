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

package com.sky.centaur.unique.application.country.executor;

import com.sky.centaur.unique.client.dto.co.CountryStateCityGetAllCo;
import com.sky.centaur.unique.domain.country.gateway.CountryGateway;
import com.sky.centaur.unique.infrastructure.country.convertor.CountryConvertor;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取所有国家详细信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Component
public class CountryStateCityGetAllExe {

  private final CountryGateway countryGateway;
  private final CountryConvertor countryConvertor;

  @Autowired
  public CountryStateCityGetAllExe(CountryGateway countryGateway,
      CountryConvertor countryConvertor) {
    this.countryGateway = countryGateway;
    this.countryConvertor = countryConvertor;
  }

  public List<CountryStateCityGetAllCo> execute() {
    return countryGateway.getCountryStateCity().stream()
        .map(country -> countryConvertor.toCountryStateCityGetAllCo(country).orElse(null)).filter(
            Objects::nonNull).toList();
  }
}