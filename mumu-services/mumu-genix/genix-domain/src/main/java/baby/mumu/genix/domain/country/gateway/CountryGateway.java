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

package baby.mumu.genix.domain.country.gateway;

import baby.mumu.genix.domain.country.City;
import baby.mumu.genix.domain.country.Country;
import baby.mumu.genix.domain.country.State;
import java.util.List;
import java.util.Optional;

/**
 * 国家领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
public interface CountryGateway {

  List<Country> getCountryStateCity();

  List<Country> getCountries();

  List<State> getStatesByCountryId(Long countryId);

  List<City> getCitiesByStateId(Long stateId);

  Optional<State> getStateById(Long stateId);

  Optional<State> getStateCitiesById(Long stateId);

  Optional<City> getCityById(Long cityId);
}
