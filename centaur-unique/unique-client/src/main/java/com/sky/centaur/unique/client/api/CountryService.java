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

package com.sky.centaur.unique.client.api;

import com.sky.centaur.unique.client.dto.CountryGetCitiesByStateIdCmd;
import com.sky.centaur.unique.client.dto.CountryGetStatesByCountryIdCmd;
import com.sky.centaur.unique.client.dto.co.CountryGetAllCo;
import com.sky.centaur.unique.client.dto.co.CountryGetCitiesByStateIdCo;
import com.sky.centaur.unique.client.dto.co.CountryGetStatesByCountryIdCo;
import com.sky.centaur.unique.client.dto.co.CountryStateCityGetAllCo;
import java.util.List;

/**
 * 国家
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
public interface CountryService {

  List<CountryStateCityGetAllCo> getCountryStateCity();

  List<CountryGetAllCo> getCountries();

  List<CountryGetStatesByCountryIdCo> getStatesByCountryId(
      CountryGetStatesByCountryIdCmd countryGetStatesByCountryIdCmd);

  List<CountryGetCitiesByStateIdCo> getCitiesByStateId(
      CountryGetCitiesByStateIdCmd countryGetCitiesByStateIdCmd);
}
