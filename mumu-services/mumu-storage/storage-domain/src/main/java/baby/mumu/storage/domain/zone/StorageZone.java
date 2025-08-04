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

package baby.mumu.storage.domain.zone;

import baby.mumu.basis.domain.BasisDomainModel;
import baby.mumu.basis.enums.StorageZonePolicyEnum;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 存储区域
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageZone extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = -2521021036158309159L;

  private Long id;

  private String code;

  private String name;

  private String description;

  private StorageZonePolicyEnum policy;

}
