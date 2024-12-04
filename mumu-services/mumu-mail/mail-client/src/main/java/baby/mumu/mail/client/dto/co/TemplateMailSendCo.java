/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.mail.client.dto.co;

import baby.mumu.basis.co.BaseClientObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模板邮件发送客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateMailSendCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 1667123436624545299L;

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
