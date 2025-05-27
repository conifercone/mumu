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
package baby.mumu.basis.po.jpa;

import baby.mumu.basis.po.PersistentObject;
import java.io.Serial;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * jpa 文档类型基础默认数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Setter
public class JpaDocumentBasisDefaultPersistentObject implements PersistentObject {

  @Serial
  private static final long serialVersionUID = -2846827195409026681L;

  @CreatedDate
  private LocalDateTime creationTime;

  @CreatedBy
  private Long founder;

  @LastModifiedBy
  private Long modifier;

  @LastModifiedDate
  private LocalDateTime modificationTime;

  @Override
  public Long getFounder() {
    return founder;
  }

  @Override
  public Long getModifier() {
    return modifier;
  }

  @Override
  public OffsetDateTime getCreationTime() {
    return Optional.ofNullable(creationTime)
      .map(creationTimeNotNull -> OffsetDateTime.of(creationTimeNotNull, ZoneOffset.UTC))
      .orElse(null);
  }

  @Override
  public OffsetDateTime getModificationTime() {
    return Optional.ofNullable(modificationTime)
      .map(modificationTimeNotNull -> OffsetDateTime.of(modificationTimeNotNull, ZoneOffset.UTC))
      .orElse(null);
  }
}
