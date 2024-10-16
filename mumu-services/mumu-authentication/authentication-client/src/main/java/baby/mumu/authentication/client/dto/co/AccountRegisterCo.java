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

import baby.mumu.basis.client.dto.co.BaseClientObject;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户信息注册客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountRegisterCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 4447904589683822023L;

  private Long id;

  @NotBlank(message = "{account.username.validation.not.blank}")
  private String username;

  @NotBlank(message = "{account.password.validation.not.blank}")
  private String password;

  private List<String> roleCodes;

  private String avatarUrl;

  private String phone;

  private SexEnum sex;

  @NotBlank(message = "{account.email.validation.not.blank}")
  private String email;

  private String timezone;

  private LanguageEnum language;

  @NotNull(message = "{account.birthday.validation.not.null}")
  private LocalDate birthday;

  private List<AccountAddressRegisterCo> addresses;

  @Data
  public static class AccountAddressRegisterCo {

    /**
     * 唯一主键
     */
    private Long id;

    /**
     * 街道地址，包含门牌号和街道信息
     */
    @Size(max = 255)
    private String street;

    /**
     * 城市信息
     */
    @Size(max = 100)
    private String city;

    /**
     * 州或省的信息
     */
    @Size(max = 100)
    private String state;

    /**
     * 邮政编码
     */
    @Size(max = 20)
    private String postalCode;

    /**
     * 国家信息
     */
    @Size(max = 100)
    private String country;
  }
}
