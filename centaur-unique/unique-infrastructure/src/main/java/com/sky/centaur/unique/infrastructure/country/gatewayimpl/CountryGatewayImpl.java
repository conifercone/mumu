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
import com.sky.centaur.unique.domain.country.Country;
import com.sky.centaur.unique.domain.country.gateway.CountryGateway;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
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
  private List<Country> countries;

  @Autowired
  public CountryGatewayImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public List<Country> getCountryStateCity() {
    return countries;
  }

  @PostConstruct
  public void loadCountries() {
    try {
      InputStream inputStream = getClass().getResourceAsStream("/countries-states-cities.json");
      this.countries = objectMapper.readValue(inputStream, new TypeReference<>() {
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
