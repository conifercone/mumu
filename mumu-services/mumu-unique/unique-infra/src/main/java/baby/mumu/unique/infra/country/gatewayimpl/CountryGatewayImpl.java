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

package baby.mumu.unique.infra.country.gatewayimpl;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.unique.domain.country.City;
import baby.mumu.unique.domain.country.Country;
import baby.mumu.unique.domain.country.State;
import baby.mumu.unique.domain.country.gateway.CountryGateway;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 国家领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Component
public class CountryGatewayImpl implements CountryGateway {

  private final ObjectMapper objectMapper;
  private List<Country> countriesStatesCities;
  private List<Country> countries;
  /**
   * key: country id value: states
   */
  private Map<Long, List<State>> countryIdMappingStates;
  /**
   * key: state id value: cities
   */
  private Map<Long, List<City>> stateIdMappingCities;
  /**
   * key: state id value: state
   */
  private Map<Long, State> stateIdMappingState;
  /**
   * key: state id value: state with cities
   */
  private Map<Long, State> stateIdMappingStateCities;
  /**
   * key: city id value: city
   */
  private Map<Long, City> cityIdMappingCity;

  @Autowired
  public CountryGatewayImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public List<Country> getCountryStateCity() {
    return countriesStatesCities;
  }

  @Override
  public List<Country> getCountries() {
    return countries;
  }

  @Override
  public List<State> getStatesByCountryId(Long countryId) {
    return Optional.ofNullable(countryId)
      .map(_ -> countryIdMappingStates.getOrDefault(countryId, new ArrayList<>()))
      .orElse(new ArrayList<>());
  }

  @Override
  public List<City> getCitiesByStateId(Long stateId) {
    return Optional.ofNullable(stateId)
      .map(stateIdNonNull -> stateIdMappingCities.getOrDefault(stateIdNonNull, new ArrayList<>()))
      .orElse(new ArrayList<>());
  }

  @Override
  public Optional<State> getStateById(Long stateId) {
    return Optional.ofNullable(stateId).map(stateIdMappingState::get);
  }

  @Override
  public Optional<State> getStateCitiesById(Long stateId) {
    return Optional.ofNullable(stateId).map(stateIdMappingStateCities::get);
  }

  @Override
  public Optional<City> getCityById(Long cityId) {
    return Optional.ofNullable(cityId).map(cityIdMappingCity::get);
  }

  @PostConstruct
  public void loadCountries() {
    try {
      InputStream inputStream = getClass().getResourceAsStream("/countries-states-cities.json");
      this.countriesStatesCities = objectMapper.readValue(inputStream, new TypeReference<>() {
      });
      this.countries = this.countriesStatesCities.stream().map(SerializationUtils::clone)
        .peek(country -> country.setStates(null))
        .toList();
      this.stateIdMappingStateCities = this.countriesStatesCities.stream()
        .flatMap(country -> country.getStates().stream())
        .collect(Collectors.toMap(State::getId, state -> state));
      this.stateIdMappingState = this.countriesStatesCities.stream().map(SerializationUtils::clone)
        .flatMap(country -> country.getStates().stream())
        .collect(Collectors.toMap(State::getId, state -> {
          state.setCities(null);
          return state;
        }));
      this.cityIdMappingCity = this.countriesStatesCities.stream()
        .flatMap(country -> country.getStates().stream())
        .flatMap(state -> state.getCities().stream())
        .collect(Collectors.toMap(City::getId, city -> city));
      this.countryIdMappingStates = this.countriesStatesCities.stream().parallel()
        .collect(Collectors.toMap(Country::getId,
          country -> Optional.ofNullable(country.getStates()).orElse(new ArrayList<>()).stream()
            .map(SerializationUtils::clone).peek(state -> state.setCities(null))
            .collect(Collectors.toList())));
      this.stateIdMappingCities = this.countriesStatesCities.stream().parallel()
        .flatMap(country -> Optional.ofNullable(country.getStates()).orElse(new ArrayList<>())
          .stream())
        .collect(Collectors.toMap(State::getId,
          state -> Optional.ofNullable(state.getCities()).orElse(new ArrayList<>())));
    } catch (Exception e) {
      throw new ApplicationException(ResponseCode.GEOGRAPHIC_DATA_LOADING_FAILED);
    }
  }
}
