/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.genix.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.genix.client.api.CountryService;
import baby.mumu.genix.client.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 国家相关接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@RestController
@Validated
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
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public ResponseWrapper<List<CountryStateCityGetAllDTO>> getCountryStateCity() {
        return ResponseWrapper.success(countryService.getCountryStateCity());
    }

    @Operation(summary = "获取国家详细信息(不包含省或州、市信息)")
    @GetMapping("/getCountries")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public ResponseWrapper<List<CountryGetAllDTO>> getCountries() {
        return ResponseWrapper.success(countryService.getCountries());
    }

    @Operation(summary = "根据国家ID获取省或州信息",
        parameters = {
            @Parameter(name = "id", description = "国家ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/getStatesByCountryId/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public ResponseWrapper<List<CountryGetStatesByCountryIdDTO>> getStatesByCountryId(
        @PathVariable Long id) {
        return ResponseWrapper.success(
            countryService.getStatesByCountryId(id));
    }

    @Operation(summary = "根据省或州ID获取城市",
        parameters = {
            @Parameter(name = "id", description = "省或州ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/getCitiesByStateId/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public ResponseWrapper<List<CountryGetCitiesByStateIdDTO>> getCitiesByStateId(
        @PathVariable Long id) {
        return ResponseWrapper.success(countryService.getCitiesByStateId(id));
    }

    @Operation(summary = "根据省或州ID获取省或州",
        parameters = {
            @Parameter(name = "id", description = "省或州ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/getStateById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public ResponseWrapper<CountryGetStateByIdDTO> getStateById(
        @PathVariable Long id) {
        return ResponseWrapper.success(countryService.getStateById(id));
    }

    @Operation(summary = "根据省或州ID获取省或州(包含下级城市)",
        parameters = {
            @Parameter(name = "id", description = "省或州ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/getStateCitiesById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public ResponseWrapper<CountryGetStateCitiesByIdDTO> getStateCitiesById(
        @PathVariable Long id) {
        return ResponseWrapper.success(countryService.getStateCitiesById(id));
    }

    @Operation(summary = "根据城市id获取城市指令",
        parameters = {
            @Parameter(name = "id", description = "城市ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/getCityById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public ResponseWrapper<CountryGetCityByIdDTO> getCityById(
        @PathVariable Long id) {
        return ResponseWrapper.success(countryService.getCityById(id));
    }
}
