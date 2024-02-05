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
package com.sky.centaur.unique.adapter.web;

import com.github.guang19.leaf.core.IdGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主键相关接口
 *
 * @author 单开宇
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/pk")
@Tag(name = "主键")
public class PrimaryKeyController {

  @Resource
  private IdGenerator snowflakeIdGenerator;

  @Operation(summary = "获取主键(雪花算法)")
  @GetMapping("/snowflake")
  @ResponseBody
  @API(status = Status.STABLE)
  public long snowflake() {
    return snowflakeIdGenerator.nextId().getId();
  }
}
