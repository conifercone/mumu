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
package baby.mumu.unique.adapter.web;

import baby.mumu.basis.response.ResultResponse;
import baby.mumu.unique.client.api.CountryService;
import baby.mumu.unique.client.dto.CountryGetCitiesByStateIdCmd;
import baby.mumu.unique.client.dto.CountryGetCityByIdCmd;
import baby.mumu.unique.client.dto.CountryGetStateByIdCmd;
import baby.mumu.unique.client.dto.CountryGetStateCitiesByIdCmd;
import baby.mumu.unique.client.dto.CountryGetStatesByCountryIdCmd;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国家相关接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
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
  @API(status = Status.STABLE, since = "1.0.5")
  public ResultResponse<List<CountryStateCityGetAllCo>> getCountryStateCity() {
    return ResultResponse.success(countryService.getCountryStateCity());
  }

  @Operation(summary = "获取国家详细信息(不包含省或州、市信息)")
  @GetMapping("/getCountries")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.5")
  public ResultResponse<List<CountryGetAllCo>> getCountries() {
    return ResultResponse.success(countryService.getCountries());
  }

  @Operation(summary = "根据国家ID获取省或州信息")
  @GetMapping("/getStatesByCountryId")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.5")
  public ResultResponse<List<CountryGetStatesByCountryIdCo>> getStatesByCountryId(@RequestBody
  CountryGetStatesByCountryIdCmd countryGetStatesByCountryIdCmd) {
    return ResultResponse.success(
        countryService.getStatesByCountryId(countryGetStatesByCountryIdCmd));
  }

  @Operation(summary = "根据省或州ID获取城市")
  @GetMapping("/getCitiesByStateId")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.5")
  public ResultResponse<List<CountryGetCitiesByStateIdCo>> getCitiesByStateId(@RequestBody
  CountryGetCitiesByStateIdCmd countryGetCitiesByStateIdCmd) {
    return ResultResponse.success(
        countryService.getCitiesByStateId(countryGetCitiesByStateIdCmd));
  }

  @Operation(summary = "根据省或州ID获取省或州")
  @GetMapping("/getStateById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.5")
  public ResultResponse<CountryGetStateByIdCo> getStateById(@RequestBody
  CountryGetStateByIdCmd countryGetStateByIdCmd) {
    return ResultResponse.success(
        countryService.getStateById(countryGetStateByIdCmd));
  }

  @Operation(summary = "根据省或州ID获取省或州(包含下级城市)")
  @GetMapping("/getStateCitiesById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.5")
  public ResultResponse<CountryGetStateCitiesByIdCo> getStateCitiesById(@RequestBody
  CountryGetStateCitiesByIdCmd countryGetStateCitiesByIdCmd) {
    return ResultResponse.success(
        countryService.getStateCitiesById(countryGetStateCitiesByIdCmd));
  }

  @Operation(summary = "根据城市id获取城市指令")
  @GetMapping("/getCityById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.5")
  public ResultResponse<CountryGetCityByIdCo> getCityById(@RequestBody
  CountryGetCityByIdCmd countryGetCityByIdCmd) {
    return ResultResponse.success(
        countryService.getCityById(countryGetCityByIdCmd));
  }
}
