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
package com.sky.centaur.unique.application.service;

import com.sky.centaur.unique.application.country.executor.CountryStateCityGetAllExe;
import com.sky.centaur.unique.client.api.CountryService;
import com.sky.centaur.unique.client.dto.co.CountryStateCityGetAllCo;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 国家
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "CountryServiceImpl")
public class CountryServiceImpl implements CountryService {

  private final CountryStateCityGetAllExe countryStateCityGetAllExe;

  @Autowired
  public CountryServiceImpl(CountryStateCityGetAllExe countryStateCityGetAllExe) {
    this.countryStateCityGetAllExe = countryStateCityGetAllExe;
  }

  @Override
  public List<CountryStateCityGetAllCo> getCountryStateCity() {
    return countryStateCityGetAllExe.execute();
  }
}
