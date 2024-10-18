/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.unique.client.api;

import baby.mumu.unique.client.dto.co.CountryGetAllCo;
import baby.mumu.unique.client.dto.co.CountryGetCitiesByStateIdCo;
import baby.mumu.unique.client.dto.co.CountryGetCityByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateCitiesByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStatesByCountryIdCo;
import baby.mumu.unique.client.dto.co.CountryStateCityGetAllCo;
import java.util.List;

/**
 * 国家
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
public interface CountryService {

  List<CountryStateCityGetAllCo> getCountryStateCity();

  List<CountryGetAllCo> getCountries();

  List<CountryGetStatesByCountryIdCo> getStatesByCountryId(Long id);

  List<CountryGetCitiesByStateIdCo> getCitiesByStateId(Long id);

  CountryGetStateByIdCo getStateById(Long id);

  CountryGetStateCitiesByIdCo getStateCitiesById(Long id);

  CountryGetCityByIdCo getCityById(Long id);
}
