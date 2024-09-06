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
package baby.mumu.unique.infrastructure.country.convertor;

import baby.mumu.unique.client.dto.co.CountryGetAllCo;
import baby.mumu.unique.client.dto.co.CountryGetCitiesByStateIdCo;
import baby.mumu.unique.client.dto.co.CountryGetCityByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateCitiesByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStatesByCountryIdCo;
import baby.mumu.unique.client.dto.co.CountryStateCityGetAllCo;
import baby.mumu.unique.domain.country.City;
import baby.mumu.unique.domain.country.Country;
import baby.mumu.unique.domain.country.State;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Country mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CountryMapper {

  CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryStateCityGetAllCo toCountryStateCityGetAllCo(Country country);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetStatesByCountryIdCo toCountryGetStatesByCountryIdCo(State state);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetCitiesByStateIdCo toCountryGetCitiesByStateIdCo(City city);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetStateByIdCo toCountryGetStateByIdCo(State state);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetStateCitiesByIdCo toCountryGetStateCitiesByIdCo(State state);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetCityByIdCo toCountryGetCityByIdCo(City city);

  @API(status = Status.STABLE, since = "2.0.0")
  CountryGetAllCo toCountryGetAllCo(Country country);
}
