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
package com.sky.centaur.log.infrastructure.operation.gatewayimpl.redis.dataobject;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 操作日志redis数据对象
 *
 * @author 单开宇
 * @since 2024-01-31
 */
@Data
@Document
public class OperationLogRedisDo {

  @Id
  @Indexed
  private String id;

  /**
   * 日志内容
   */
  @Searchable
  private String content;

  /**
   * 操作日志的执行人
   */
  @Searchable
  private String operator;

  /**
   * 操作日志绑定的业务对象标识
   */
  @Searchable
  private String bizNo;

  /**
   * 操作日志的种类
   */
  @Searchable
  private String category;

  /**
   * 扩展参数，记录操作日志的修改详情
   */
  @Searchable
  private String detail;

  /**
   * 操作日志成功的文本模板
   */
  @Searchable
  private String success;

  /**
   * 操作日志失败的文本模板
   */
  @Searchable
  private String fail;

  /**
   * 操作日志的操作时间
   */
  @Searchable
  private LocalDateTime operatingTime;

  /**
   * 存活时间
   */
  @TimeToLive
  private Long ttl = 5L;
}
