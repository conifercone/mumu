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

import com.sky.centaur.unique.client.dto.CountryGetCityByIdCmd;
import com.sky.centaur.unique.client.dto.co.CountryGetCityByIdCo;
import com.sky.centaur.unique.domain.country.gateway.CountryGateway;
import com.sky.centaur.unique.infrastructure.country.convertor.CountryConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 根据城市id获取城市指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Component
public class CountryGetCityByIdCmdExe {

  private final CountryGateway countryGateway;
  private final CountryConvertor countryConvertor;

  @Autowired
  public CountryGetCityByIdCmdExe(CountryGateway countryGateway,
      CountryConvertor countryConvertor) {
    this.countryGateway = countryGateway;
    this.countryConvertor = countryConvertor;
  }

  public CountryGetCityByIdCo execute(
      CountryGetCityByIdCmd countryGetCityByIdCmd) {
    Assert.notNull(countryGetCityByIdCmd, "CountryGetCityByIdCmd cannot be null");
    return Optional.ofNullable(countryGetCityByIdCmd.getCityId())
        .flatMap(countryGateway::getCityById).flatMap(countryConvertor::toCountryGetCityByIdCo)
        .orElse(null);
  }
}