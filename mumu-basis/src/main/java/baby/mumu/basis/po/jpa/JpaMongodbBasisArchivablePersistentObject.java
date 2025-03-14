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

import baby.mumu.basis.po.ArchivablePersistentObject;
import jakarta.persistence.EntityListeners;
import java.io.Serial;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * jpa mongodb 基础可存档数据对象
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.2.0
 */
@SuppressWarnings("unused")
@EntityListeners(AuditingEntityListener.class)
@Setter
public class JpaMongodbBasisArchivablePersistentObject extends
  JpaMongodbBasisDefaultPersistentObject implements
  ArchivablePersistentObject {

  @Serial
  private static final long serialVersionUID = -1101323311607199590L;

  private boolean archived;

  @Override
  public boolean isArchived() {
    return archived;
  }
}
