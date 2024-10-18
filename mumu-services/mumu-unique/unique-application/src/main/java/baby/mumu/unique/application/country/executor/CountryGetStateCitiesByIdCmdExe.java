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
package baby.mumu.unique.application.country.executor;

import baby.mumu.unique.client.dto.co.CountryGetStateCitiesByIdCo;
import baby.mumu.unique.domain.country.gateway.CountryGateway;
import baby.mumu.unique.infrastructure.country.convertor.CountryConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 根据省或州ID获取省或州（包含下级城市）指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Component
public class CountryGetStateCitiesByIdCmdExe {

  private final CountryGateway countryGateway;
  private final CountryConvertor countryConvertor;

  @Autowired
  public CountryGetStateCitiesByIdCmdExe(CountryGateway countryGateway,
      CountryConvertor countryConvertor) {
    this.countryGateway = countryGateway;
    this.countryConvertor = countryConvertor;
  }

  public CountryGetStateCitiesByIdCo execute(
      Long id) {
    Assert.notNull(id, "id cannot be null");
    return Optional.of(id)
        .flatMap(countryGateway::getStateCitiesById)
        .flatMap(countryConvertor::toCountryGetStateCitiesByIdCo)
        .orElse(null);
  }
}
