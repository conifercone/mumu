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
package com.sky.centaur.unique.infrastructure.country.convertor;

import com.sky.centaur.unique.client.dto.co.CountryGetAllCo;
import com.sky.centaur.unique.client.dto.co.CountryStateCityGetAllCo;
import com.sky.centaur.unique.domain.country.Country;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;

/**
 * 国家对象转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Component
public class CountryConvertor {

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.5")
  public Optional<CountryStateCityGetAllCo> toCountryStateCityGetAllCo(
      Country country) {
    return Optional.ofNullable(country).map(CountryMapper.INSTANCE::toCountryStateCityGetAllCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.5")
  public Optional<CountryGetAllCo> toCountryGetAllCo(
      Country country) {
    return Optional.ofNullable(country).map(CountryMapper.INSTANCE::toCountryGetAllCo);
  }
}
