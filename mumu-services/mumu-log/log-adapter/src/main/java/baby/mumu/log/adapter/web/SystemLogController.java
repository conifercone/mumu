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
package baby.mumu.log.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.log.client.api.SystemLogService;
import baby.mumu.log.client.cmds.SystemLogFindAllCmd;
import baby.mumu.log.client.cmds.SystemLogSubmitCmd;
import baby.mumu.log.client.dto.SystemLogFindAllDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志相关
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
@RestController
@Validated
@RequestMapping("/system")
@Tag(name = "系统日志管理")
public class SystemLogController {

  private final SystemLogService systemLogService;

  @Autowired
  public SystemLogController(SystemLogService systemLogService) {
    this.systemLogService = systemLogService;
  }

  @Operation(summary = "提交日志")
  @PostMapping("/submit")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void submit(@RequestBody SystemLogSubmitCmd systemLogSubmitCmd) {
    systemLogService.submit(systemLogSubmitCmd);
  }

  @Operation(summary = "分页查询所有系统日志")
  @GetMapping("/findAll")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<SystemLogFindAllDTO> findAll(
    @ModelAttribute @Validated SystemLogFindAllCmd systemLogFindAllCmd) {
    return systemLogService.findAll(systemLogFindAllCmd);
  }
}
