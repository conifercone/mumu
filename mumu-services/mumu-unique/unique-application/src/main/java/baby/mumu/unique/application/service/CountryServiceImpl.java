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
package baby.mumu.unique.application.service;

import baby.mumu.unique.application.country.executor.CountryGetAllExe;
import baby.mumu.unique.application.country.executor.CountryGetCitiesByStateIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetCityByIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetStateByIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetStateCitiesByIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetStatesByCountryIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryStateCityGetAllExe;
import baby.mumu.unique.client.api.CountryService;
import baby.mumu.unique.client.dto.co.CountryGetAllCo;
import baby.mumu.unique.client.dto.co.CountryGetCitiesByStateIdCo;
import baby.mumu.unique.client.dto.co.CountryGetCityByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateCitiesByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStatesByCountryIdCo;
import baby.mumu.unique.client.dto.co.CountryStateCityGetAllCo;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 国家
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Service
@Observed(name = "CountryServiceImpl")
public class CountryServiceImpl implements CountryService {

  private final CountryStateCityGetAllExe countryStateCityGetAllExe;
  private final CountryGetAllExe countryGetAllExe;
  private final CountryGetStatesByCountryIdCmdExe countryGetStatesByCountryIdCmdExe;
  private final CountryGetCitiesByStateIdCmdExe countryGetCitiesByStateIdCmdExe;
  private final CountryGetStateByIdCmdExe countryGetStateByIdCmdExe;
  private final CountryGetStateCitiesByIdCmdExe countryGetStateCitiesByIdCmdExe;
  private final CountryGetCityByIdCmdExe countryGetCityByIdCmdExe;

  @Autowired
  public CountryServiceImpl(CountryStateCityGetAllExe countryStateCityGetAllExe,
    CountryGetAllExe countryGetAllExe,
    CountryGetStatesByCountryIdCmdExe countryGetStatesByCountryIdCmdExe,
    CountryGetCitiesByStateIdCmdExe countryGetCitiesByStateIdCmdExe,
    CountryGetStateByIdCmdExe countryGetStateByIdCmdExe,
    CountryGetStateCitiesByIdCmdExe countryGetStateCitiesByIdCmdExe,
    CountryGetCityByIdCmdExe countryGetCityByIdCmdExe) {
    this.countryStateCityGetAllExe = countryStateCityGetAllExe;
    this.countryGetAllExe = countryGetAllExe;
    this.countryGetStatesByCountryIdCmdExe = countryGetStatesByCountryIdCmdExe;
    this.countryGetCitiesByStateIdCmdExe = countryGetCitiesByStateIdCmdExe;
    this.countryGetStateByIdCmdExe = countryGetStateByIdCmdExe;
    this.countryGetStateCitiesByIdCmdExe = countryGetStateCitiesByIdCmdExe;
    this.countryGetCityByIdCmdExe = countryGetCityByIdCmdExe;
  }

  @Override
  public List<CountryStateCityGetAllCo> getCountryStateCity() {
    return countryStateCityGetAllExe.execute();
  }

  @Override
  public List<CountryGetAllCo> getCountries() {
    return countryGetAllExe.execute();
  }

  @Override
  public List<CountryGetStatesByCountryIdCo> getStatesByCountryId(
    Long id) {
    return countryGetStatesByCountryIdCmdExe.execute(id);
  }

  @Override
  public List<CountryGetCitiesByStateIdCo> getCitiesByStateId(
    Long id) {
    return countryGetCitiesByStateIdCmdExe.execute(id);
  }

  @Override
  public CountryGetStateByIdCo getStateById(Long id) {
    return countryGetStateByIdCmdExe.execute(id);
  }

  @Override
  public CountryGetStateCitiesByIdCo getStateCitiesById(
    Long id) {
    return countryGetStateCitiesByIdCmdExe.execute(id);
  }

  @Override
  public CountryGetCityByIdCo getCityById(Long id) {
    return countryGetCityByIdCmdExe.execute(id);
  }
}
