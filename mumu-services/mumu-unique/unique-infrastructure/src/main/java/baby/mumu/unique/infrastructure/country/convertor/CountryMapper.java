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
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Country mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper {

  CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryStateCityGetAllDTO toCountryStateCityGetAllDTO(Country country);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetStatesByCountryIdDTO toCountryGetStatesByCountryIdDTO(State state);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetCitiesByStateIdDTO toCountryGetCitiesByStateIdDTO(City city);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetStateByIdDTO toCountryGetStateByIdDTO(State state);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetStateCitiesByIdDTO toCountryGetStateCitiesByIdDTO(State state);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetCityByIdDTO toCountryGetCityByIdDTO(City city);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetAllDTO toCountryGetAllDTO(Country country);
}
