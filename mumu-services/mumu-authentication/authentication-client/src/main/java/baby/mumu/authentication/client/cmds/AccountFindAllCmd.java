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
package baby.mumu.authentication.client.cmds;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Data;

/**
 * 分页查询所有账户指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
public class AccountFindAllCmd {

  /**
   * 账户id
   */
  private Long id;

  /**
   * 账户名
   */
  private String username;

  /**
   * 账户角色ID集合
   */
  private List<Long> roleIds;

  /**
   * 电话
   */
  private String phone;

  /**
   * 电子邮件
   */
  private String email;

  @Min(value = 1, message = "{current.validation.min.size}")
  private Integer current = 1;

  @Min(value = 1, message = "{page.size.validation.min.size}")
  private Integer pageSize = 10;
}
