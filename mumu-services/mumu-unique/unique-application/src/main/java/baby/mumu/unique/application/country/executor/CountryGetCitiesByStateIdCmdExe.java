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

package baby.mumu.unique.application.country.executor;

import baby.mumu.unique.client.dto.CountryGetCitiesByStateIdDTO;
import baby.mumu.unique.domain.country.gateway.CountryGateway;
import baby.mumu.unique.infra.country.convertor.CountryConvertor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 根据省或州ID获取城市指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Component
public class CountryGetCitiesByStateIdCmdExe {

  private final CountryGateway countryGateway;
  private final CountryConvertor countryConvertor;

  @Autowired
  public CountryGetCitiesByStateIdCmdExe(CountryGateway countryGateway,
    CountryConvertor countryConvertor) {
    this.countryGateway = countryGateway;
    this.countryConvertor = countryConvertor;
  }

  public List<CountryGetCitiesByStateIdDTO> execute(
    Long id) {
    Assert.notNull(id, "id cannot be null");
    return Optional.of(id)
      .map(stateId -> countryGateway.getCitiesByStateId(stateId).stream()
        .map(city -> countryConvertor.toCountryGetCitiesByStateIdDTO(city).orElse(null))
        .filter(
          Objects::nonNull).toList()).orElse(new ArrayList<>());
  }
}
