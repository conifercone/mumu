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

import com.sankuai.inf.leaf.service.SegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 唯一性主键相关接口
 *
 * @author 单开宇
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/pk")
@Tag(name = "主键管理")
public class PrimaryKeyController {

  @Resource
  private SegmentService segmentService;

  @Operation(summary = "号段模式获取主键")
  @GetMapping("/segment")
  @ResponseBody
  @API(status = Status.STABLE)
  public long segment(@RequestParam("key") String key) {
    return segmentService.getId(key).getId();
  }
}
