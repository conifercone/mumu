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

import java.util.List;
import lombok.Data;

/**
 * 下载所有权限数据（包含权限路径）数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.6.0
 */
@Data
public class PermissionIncludePathDownloadAllDTO {

  private Long id;

  private String code;

  private String name;

  private String description;

  private boolean hasDescendant;

  private List<PermissionPathDTO> descendants;

  @Data
  public static class PermissionPathDTO {

    private Long ancestorId;

    private Long descendantId;

    private Long depth;
  }
}
