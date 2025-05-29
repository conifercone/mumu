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
import java.io.Serial;
import lombok.Setter;

/**
 * jpa 可缓存的基础可存档数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Setter
public class JpaCacheableBasisArchivablePersistentObject extends
  JpaCacheableBasisDefaultPersistentObject implements
  ArchivablePersistentObject {

  @Serial
  private static final long serialVersionUID = -9004328530785061008L;

  private boolean archived;

  @Override
  public boolean isArchived() {
    return archived;
  }
}
