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
package baby.mumu.authentication.domain.role;

import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.domain.BasisDomainModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 角色领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Metamodel
public class Role extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = 4390244868463637644L;

  /**
   * 角色id
   */
  private Long id;

  /**
   * 角色编码
   */
  private String code;

  /**
   * 角色名称
   */
  private String name;

  /**
   * 角色权限
   */
  private List<Authority> authorities;

  /**
   * 角色权限后代
   */
  private List<Authority> descendantAuthorities;

}
