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

package baby.mumu.storage.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.storage.client.api.StorageZoneService;
import baby.mumu.storage.client.cmds.StorageZoneAddCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 存储区域管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@RestController
@Validated
@RequestMapping("/zone")
@Tag(name = "存储区域管理")
public class StorageZoneController {

    private final StorageZoneService storageZoneService;

    public StorageZoneController(StorageZoneService storageZoneService) {
        this.storageZoneService = storageZoneService;
    }

    @Operation(summary = "新增存储区域")
    @PostMapping("/add")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.13.0")
    public ResponseWrapper<Long> add(@RequestBody StorageZoneAddCmd storageZoneAddCmd) {
        return ResponseWrapper.success(storageZoneService.add(storageZoneAddCmd));
    }
}
