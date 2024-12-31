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
package baby.mumu.basis.po.jpa;

import baby.mumu.basis.po.PersistentObject;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.time.OffsetDateTime;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * jpa基础默认数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter
public class JpaBasisDefaultPersistentObject implements PersistentObject {

  @Serial
  private static final long serialVersionUID = -6208186991995932595L;

  @CreationTimestamp
  @Column(name = "creation_time", updatable = false, nullable = false)
  private OffsetDateTime creationTime;

  @CreatedBy
  @Column(name = "founder", updatable = false, nullable = false)
  private Long founder;

  @LastModifiedBy
  @Column(name = "modifier", nullable = false)
  private Long modifier;

  @UpdateTimestamp
  @Column(name = "modification_time", nullable = false)
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
