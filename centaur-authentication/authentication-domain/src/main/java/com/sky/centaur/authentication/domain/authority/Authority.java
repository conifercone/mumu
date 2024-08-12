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

package com.sky.centaur.authentication.domain.authority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sky.centaur.basis.constants.CommonConstants;
import com.sky.centaur.basis.domain.BasisDomainModel;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限领域模型
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Authority extends BasisDomainModel implements GrantedAuthority {

  @Serial
  private static final long serialVersionUID = -5474154746791467326L;

  private Long id;

  @Size(max = 50, message = "{authority.code.validation.size}")
  private String code;

  @Size(max = 200, message = "{authority.name.validation.size}")
  private String name;

  @Override
  public String getAuthority() {
    return CommonConstants.AUTHORITY_PREFIX.concat(code);
  }
}
