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

import com.sky.centaur.unique.client.dto.CountryGetStateByIdCmd;
import com.sky.centaur.unique.client.dto.co.CountryGetStateByIdCo;
import com.sky.centaur.unique.domain.country.gateway.CountryGateway;
import com.sky.centaur.unique.infrastructure.country.convertor.CountryConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 根据省或州ID获取省或州指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Component
public class CountryGetStateByIdCmdExe {

  private final CountryGateway countryGateway;
  private final CountryConvertor countryConvertor;

  @Autowired
  public CountryGetStateByIdCmdExe(CountryGateway countryGateway,
      CountryConvertor countryConvertor) {
    this.countryGateway = countryGateway;
    this.countryConvertor = countryConvertor;
  }

  public CountryGetStateByIdCo execute(
      CountryGetStateByIdCmd countryGetStateByIdCmd) {
    Assert.notNull(countryGetStateByIdCmd, "CountryGetStateByIdCmd cannot be null");
    return Optional.ofNullable(countryGetStateByIdCmd.getStateId())
        .flatMap(countryGateway::getStateById).flatMap(countryConvertor::toCountryGetStateByIdCo)
        .orElse(null);
  }
}
