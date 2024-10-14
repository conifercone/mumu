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
package baby.mumu.basis.dataobject.jpa;

import baby.mumu.basis.dataobject.DataObject;
import jakarta.persistence.EntityListeners;
import java.io.Serial;
import java.time.OffsetDateTime;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * jpa mongodb 基础默认数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@EntityListeners(AuditingEntityListener.class)
@Setter
public class JpaMongodbBasisDefaultDataObject implements DataObject {

  @Serial
  private static final long serialVersionUID = -2846827195409026681L;

  @CreationTimestamp
  private OffsetDateTime creationTime;

  @CreatedBy
  private Long founder;

  @LastModifiedBy
  private Long modifier;

  @UpdateTimestamp
  private OffsetDateTime modificationTime;

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
    return creationTime;
  }

  @Override
  public OffsetDateTime getModificationTime() {
    return modificationTime;
  }
}
