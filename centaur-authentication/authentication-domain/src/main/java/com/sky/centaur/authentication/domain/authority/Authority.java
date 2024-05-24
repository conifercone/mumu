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
import com.sky.centaur.basis.domain.BasisDomainModel;
import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限领域模型
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Authority extends BasisDomainModel implements GrantedAuthority {

  @Serial
  private static final long serialVersionUID = -5474154746791467326L;
  
  private Long id;
  private String code;
  private String name;

  /**
   * all properties constructor
   */
  public Authority(Long id, String code, String name) {
    this.id = id;
    this.code = code;
    this.name = name;
  }

  @Override
  public String getAuthority() {
    return code;
  }
}
