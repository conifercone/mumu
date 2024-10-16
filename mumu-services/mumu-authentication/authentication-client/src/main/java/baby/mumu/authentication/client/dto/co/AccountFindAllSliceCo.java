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
package baby.mumu.authentication.client.dto.co;

import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.client.dto.co.BaseClientObject;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户分页查询客户端对象（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Metamodel
public class AccountFindAllSliceCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 2643022626369675463L;

  /**
   * 账户id
   */
  private Long id;

  /**
   * 账户名
   */
  private String username;

  /**
   * 是否启用
   */
  private Boolean enabled;

  /**
   * 凭证未过期
   */
  private Boolean credentialsNonExpired;

  /**
   * 帐户未锁定
   */
  private Boolean accountNonLocked;

  /**
   * 帐号未过期
   */
  private Boolean accountNonExpired;

  /**
   * 账户角色
   */
  private List<AccountFindAllRoleCo> roles;

  /**
   * 头像地址
   */
  private String avatarUrl;

  /**
   * 电话
   */
  private String phone;

  /**
   * 性别
   */
  private SexEnum sex;

  /**
   * 电子邮件
   */
  private String email;

  /**
   * 时区
   */
  private String timezone;

  /**
   * 语言偏好
   */
  private LanguageEnum language;

  /**
   * 生日
   */
  private LocalDate birthday;

  /**
   * 地址
   */
  private List<AccountAddress> addresses;


  @Data
  public static class AccountFindAllRoleCo {

    private Long id;

    private String name;

    private String code;

    private List<AccountFindAllAuthorityCo> authorities;
  }

  @Data
  public static class AccountFindAllAuthorityCo {

    /**
     * 权限id
     */
    private Long id;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限名称
     */
    private String name;
  }
}
