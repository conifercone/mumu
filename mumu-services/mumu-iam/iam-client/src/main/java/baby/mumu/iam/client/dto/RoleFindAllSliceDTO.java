/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.iam.client.dto;

import baby.mumu.basis.dto.BaseDataTransferObject;
import java.io.Serial;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询数据传输对象（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleFindAllSliceDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = 354147586039980402L;


  private Long id;

  private String name;

  private String code;

  private String description;

  private List<RoleFindAllSlicePermissionCo> permissions;

  /**
   * 有后代角色
   */
  private boolean hasDescendant;

  @Data
  public static class RoleFindAllSlicePermissionCo {

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

    private String description;

    /**
     * 有后代权限
     */
    private boolean hasDescendant;
  }
}
