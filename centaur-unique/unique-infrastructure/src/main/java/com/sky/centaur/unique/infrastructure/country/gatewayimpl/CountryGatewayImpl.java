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
package com.sky.centaur.unique.infrastructure.country.gatewayimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.unique.domain.country.City;
import com.sky.centaur.unique.domain.country.Country;
import com.sky.centaur.unique.domain.country.State;
import com.sky.centaur.unique.domain.country.gateway.CountryGateway;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Component
public class CountryGatewayImpl implements CountryGateway {

  private final ObjectMapper objectMapper;
  private List<Country> countriesStatesCities;
  private List<Country> countries;
  /**
   * key: country id value: states
   */
  private Map<Long, List<State>> countryStates;
  /**
   * key: state id value: cities
   */
  private Map<Long, List<City>> stateCities;

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
        .map(countryIdNonNull -> countryStates.getOrDefault(countryId, new ArrayList<>()))
        .orElse(new ArrayList<>());
  }

  @Override
  public List<City> getCitiesByStateId(Long stateId) {
    return Optional.ofNullable(stateId)
        .map(stateIdNonNull -> stateCities.getOrDefault(stateIdNonNull, new ArrayList<>()))
        .orElse(new ArrayList<>());
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
      this.countryStates = this.countriesStatesCities.stream().parallel()
          .collect(Collectors.toMap(Country::getId,
              country -> Optional.ofNullable(country.getStates()).orElse(new ArrayList<>()).stream()
                  .map(SerializationUtils::clone).peek(state -> state.setCities(null))
                  .collect(Collectors.toList())));
      this.stateCities = this.countriesStatesCities.stream().parallel()
          .flatMap(country -> Optional.ofNullable(country.getStates()).orElse(new ArrayList<>())
              .stream())
          .collect(Collectors.toMap(State::getId,
              state -> Optional.ofNullable(state.getCities()).orElse(new ArrayList<>())));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
