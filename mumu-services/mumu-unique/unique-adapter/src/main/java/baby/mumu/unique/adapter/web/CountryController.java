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
package baby.mumu.unique.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResultResponse;
import baby.mumu.unique.client.api.CountryService;
import baby.mumu.unique.client.dto.co.CountryGetAllCo;
import baby.mumu.unique.client.dto.co.CountryGetCitiesByStateIdCo;
import baby.mumu.unique.client.dto.co.CountryGetCityByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStateCitiesByIdCo;
import baby.mumu.unique.client.dto.co.CountryGetStatesByCountryIdCo;
import baby.mumu.unique.client.dto.co.CountryStateCityGetAllCo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国家相关接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@RestController
@RequestMapping("/country")
@Tag(name = "国家管理")
public class CountryController {

  private final CountryService countryService;

  @Autowired
  public CountryController(CountryService countryService) {
    this.countryService = countryService;
  }

  @Operation(summary = "获取国家详细信息")
  @GetMapping("/getCountryStateCity")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public ResultResponse<List<CountryStateCityGetAllCo>> getCountryStateCity() {
    return ResultResponse.success(countryService.getCountryStateCity());
  }

  @Operation(summary = "获取国家详细信息(不包含省或州、市信息)")
  @GetMapping("/getCountries")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public ResultResponse<List<CountryGetAllCo>> getCountries() {
    return ResultResponse.success(countryService.getCountries());
  }

  @Operation(summary = "根据国家ID获取省或州信息")
  @GetMapping("/getStatesByCountryId/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public ResultResponse<List<CountryGetStatesByCountryIdCo>> getStatesByCountryId(
      @PathVariable(value = "id")
      Long id) {
    return ResultResponse.success(
        countryService.getStatesByCountryId(id));
  }

  @Operation(summary = "根据省或州ID获取城市")
  @GetMapping("/getCitiesByStateId/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public ResultResponse<List<CountryGetCitiesByStateIdCo>> getCitiesByStateId(
      @PathVariable(value = "id")
      Long id) {
    return ResultResponse.success(countryService.getCitiesByStateId(id));
  }

  @Operation(summary = "根据省或州ID获取省或州")
  @GetMapping("/getStateById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public ResultResponse<CountryGetStateByIdCo> getStateById(@PathVariable(value = "id")
  Long id) {
    return ResultResponse.success(countryService.getStateById(id));
  }

  @Operation(summary = "根据省或州ID获取省或州(包含下级城市)")
  @GetMapping("/getStateCitiesById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public ResultResponse<CountryGetStateCitiesByIdCo> getStateCitiesById(@PathVariable(value = "id")
  Long id) {
    return ResultResponse.success(countryService.getStateCitiesById(id));
  }

  @Operation(summary = "根据城市id获取城市指令")
  @GetMapping("/getCityById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public ResultResponse<CountryGetCityByIdCo> getCityById(@PathVariable(value = "id")
  Long id) {
    return ResultResponse.success(countryService.getCityById(id));
  }
}
