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

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.client.dto.co.BaseClientObject;
import java.io.Serial;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 已归档角色查询客户端对象（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Metamodel
public class RoleArchivedFindAllSliceCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = -7150752089612689612L;

  private Long id;

  private String name;

  private String code;

  private List<RoleArchivedFindAllSliceAuthorityCo> authorities;

  @Data
  public static class RoleArchivedFindAllSliceAuthorityCo {

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

    /**
     * 有后代权限
     */
    private boolean hasDescendant;
  }
}
