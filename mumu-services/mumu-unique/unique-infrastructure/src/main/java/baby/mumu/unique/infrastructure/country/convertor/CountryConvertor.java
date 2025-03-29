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
package baby.mumu.unique.infrastructure.country.convertor;

import baby.mumu.unique.client.dto.CountryGetAllDTO;
import baby.mumu.unique.client.dto.CountryGetCitiesByStateIdDTO;
import baby.mumu.unique.client.dto.CountryGetCityByIdDTO;
import baby.mumu.unique.client.dto.CountryGetStateByIdDTO;
import baby.mumu.unique.client.dto.CountryGetStateCitiesByIdDTO;
import baby.mumu.unique.client.dto.CountryGetStatesByCountryIdDTO;
import baby.mumu.unique.client.dto.CountryStateCityGetAllDTO;
import baby.mumu.unique.domain.country.City;
import baby.mumu.unique.domain.country.Country;
import baby.mumu.unique.domain.country.State;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;

/**
 * 国家对象转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Component
public class CountryConvertor {

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<CountryStateCityGetAllDTO> toCountryStateCityGetAllDTO(
    Country country) {
    return Optional.ofNullable(country).map(CountryMapper.INSTANCE::toCountryStateCityGetAllDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<CountryGetStatesByCountryIdDTO> toCountryGetStatesByCountryIdDTO(
    State state) {
    return Optional.ofNullable(state).map(CountryMapper.INSTANCE::toCountryGetStatesByCountryIdDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<CountryGetCitiesByStateIdDTO> toCountryGetCitiesByStateIdDTO(
    City city) {
    return Optional.ofNullable(city).map(CountryMapper.INSTANCE::toCountryGetCitiesByStateIdDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<CountryGetStateByIdDTO> toCountryGetStateByIdDTO(
    State state) {
    return Optional.ofNullable(state).map(CountryMapper.INSTANCE::toCountryGetStateByIdDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<CountryGetStateCitiesByIdDTO> toCountryGetStateCitiesByIdDTO(
    State state) {
    return Optional.ofNullable(state)
      .map(CountryMapper.INSTANCE::toCountryGetStateCitiesByIdDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<CountryGetCityByIdDTO> toCountryGetCityByIdDTO(
    City city) {
    return Optional.ofNullable(city)
      .map(CountryMapper.INSTANCE::toCountryGetCityByIdDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<CountryGetAllDTO> toCountryGetAllDTO(
    Country country) {
    return Optional.ofNullable(country).map(CountryMapper.INSTANCE::toCountryGetAllDTO);
  }
}
