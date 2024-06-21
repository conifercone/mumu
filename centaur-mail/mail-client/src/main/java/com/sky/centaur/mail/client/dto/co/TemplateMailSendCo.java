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
package com.sky.centaur.mail.client.dto.co;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;

/**
 * 模板邮件发送客户端对象
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateMailSendCo {

  /**
   * 模板地址
   */
  private String address;

  /**
   * 模板邮件名称
   */
  private String name;

  /**
   * 邮件内容数据
   */
  private Map<String, Object> data;

  /**
   * 发件人
   */
  private String from;

  /**
   * 收件人
   */
  private String to;

  /**
   * 主题
   */
  private String subject;
}
