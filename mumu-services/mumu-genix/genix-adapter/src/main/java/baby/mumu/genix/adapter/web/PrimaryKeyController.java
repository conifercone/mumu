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
import baby.mumu.genix.client.api.PrimaryKeyService;
import baby.mumu.genix.client.dto.PrimaryKeySnowflakeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主键相关接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@RestController
@Validated
@RequestMapping("/pk")
@Tag(name = "主键")
public class PrimaryKeyController {

  private final PrimaryKeyService primaryKeyService;

  @Autowired
  public PrimaryKeyController(PrimaryKeyService primaryKeyService) {
    this.primaryKeyService = primaryKeyService;
  }

  @Operation(summary = "获取主键(雪花算法)")
  @GetMapping("/snowflake")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public PrimaryKeySnowflakeDTO snowflake() {
    return primaryKeyService.snowflake();
  }
}
