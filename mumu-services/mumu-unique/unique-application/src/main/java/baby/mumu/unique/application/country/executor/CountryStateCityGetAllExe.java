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

import baby.mumu.unique.client.dto.CountryStateCityGetAllDTO;
import baby.mumu.unique.domain.country.gateway.CountryGateway;
import baby.mumu.unique.infrastructure.country.convertor.CountryConvertor;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取所有国家详细信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Component
public class CountryStateCityGetAllExe {

  private final CountryGateway countryGateway;
  private final CountryConvertor countryConvertor;

  @Autowired
  public CountryStateCityGetAllExe(CountryGateway countryGateway,
    CountryConvertor countryConvertor) {
    this.countryGateway = countryGateway;
    this.countryConvertor = countryConvertor;
  }

  public List<CountryStateCityGetAllDTO> execute() {
    return countryGateway.getCountryStateCity().stream()
      .map(country -> countryConvertor.toCountryStateCityGetAllDTO(country).orElse(null)).filter(
        Objects::nonNull).toList();
  }
}
