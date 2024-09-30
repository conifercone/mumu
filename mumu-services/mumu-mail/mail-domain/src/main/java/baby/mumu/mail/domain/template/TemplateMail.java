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
package baby.mumu.mail.domain.template;

import baby.mumu.basis.domain.BasisDomainModel;
import java.io.Serial;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模板邮件领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateMail extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = -952362684090305637L;

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
