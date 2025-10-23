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

package baby.mumu.genix.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.kotlin.tools.TimeUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Set;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 时间相关接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.7.0
 */
@RestController
@Validated
@RequestMapping("/time")
@Tag(name = "时间管理")
public class TimeController {

  @Operation(summary = "获取可用时区列表")
  @GetMapping("/timezone/available")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.7.0")
  public ResponseWrapper<Set<String>> available() {
    return ResponseWrapper.success(ZoneId.getAvailableZoneIds());
  }

  @Operation(summary = "获取当前服务器时间")
  @GetMapping("/serverTime")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.7.0")
  public ResponseWrapper<OffsetDateTime> serverTime(@RequestParam("zoneId") String zoneId) {
    if (!TimeUtils.isValidTimeZone(zoneId)) {
      throw new ApplicationException(ResponseCode.TIME_ZONE_IS_NOT_AVAILABLE);
    } else {
      return ResponseWrapper.success(OffsetDateTime.now(ZoneId.of(zoneId)));
    }
  }
}
