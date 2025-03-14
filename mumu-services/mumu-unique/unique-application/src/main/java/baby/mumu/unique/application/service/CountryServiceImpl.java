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
package baby.mumu.unique.application.service;

import baby.mumu.unique.application.country.executor.CountryGetAllExe;
import baby.mumu.unique.application.country.executor.CountryGetCitiesByStateIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetCityByIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetStateByIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetStateCitiesByIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryGetStatesByCountryIdCmdExe;
import baby.mumu.unique.application.country.executor.CountryStateCityGetAllExe;
import baby.mumu.unique.client.api.CountryService;
import baby.mumu.unique.client.dto.CountryGetAllDTO;
import baby.mumu.unique.client.dto.CountryGetCitiesByStateIdDTO;
import baby.mumu.unique.client.dto.CountryGetCityByIdDTO;
import baby.mumu.unique.client.dto.CountryGetStateByIdDTO;
import baby.mumu.unique.client.dto.CountryGetStateCitiesByIdDTO;
import baby.mumu.unique.client.dto.CountryGetStatesByCountryIdDTO;
import baby.mumu.unique.client.dto.CountryStateCityGetAllDTO;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 国家
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
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
  public List<CountryStateCityGetAllDTO> getCountryStateCity() {
    return countryStateCityGetAllExe.execute();
  }

  @Override
  public List<CountryGetAllDTO> getCountries() {
    return countryGetAllExe.execute();
  }

  @Override
  public List<CountryGetStatesByCountryIdDTO> getStatesByCountryId(
    Long id) {
    return countryGetStatesByCountryIdCmdExe.execute(id);
  }

  @Override
  public List<CountryGetCitiesByStateIdDTO> getCitiesByStateId(
    Long id) {
    return countryGetCitiesByStateIdCmdExe.execute(id);
  }

  @Override
  public CountryGetStateByIdDTO getStateById(Long id) {
    return countryGetStateByIdCmdExe.execute(id);
  }

  @Override
  public CountryGetStateCitiesByIdDTO getStateCitiesById(
    Long id) {
    return countryGetStateCitiesByIdCmdExe.execute(id);
  }

  @Override
  public CountryGetCityByIdDTO getCityById(Long id) {
    return countryGetCityByIdCmdExe.execute(id);
  }
}
