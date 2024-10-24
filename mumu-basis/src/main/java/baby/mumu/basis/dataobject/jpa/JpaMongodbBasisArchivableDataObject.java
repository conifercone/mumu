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

import baby.mumu.basis.dataobject.ArchivableDataObject;
import jakarta.persistence.EntityListeners;
import java.io.Serial;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * jpa mongodb 基础可存档数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@EntityListeners(AuditingEntityListener.class)
@Setter
public class JpaMongodbBasisArchivableDataObject extends JpaMongodbBasisDefaultDataObject implements
    ArchivableDataObject {

  @Serial
  private static final long serialVersionUID = -1101323311607199590L;

  private Boolean archived;

  @Override
  public Boolean isArchived() {
    return archived;
  }
}
