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

package baby.mumu.basis.domain;

import java.io.Serial;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 基础领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@ToString
public abstract class BasisDomainModel implements DomainModel {

  @Serial
  private static final long serialVersionUID = 4669900889494369438L;

  /**
   * 创建时间
   */
  private OffsetDateTime creationTime;

  /**
   * 创建人
   */
  private Long founder;

  /**
   * 修改人
   */
  private Long modifier;

  /**
   * 修改时间
   */
  private OffsetDateTime modificationTime;

  /**
   * 已归档
   */
  private boolean archived;
}
