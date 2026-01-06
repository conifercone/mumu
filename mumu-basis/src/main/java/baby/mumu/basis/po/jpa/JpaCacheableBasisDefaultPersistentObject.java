/*
 * Copyright (c) 2024-2026, the original author or authors.
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
import lombok.Setter;

import java.io.Serial;
import java.time.OffsetDateTime;

/**
 * jpa 可缓存的基础默认数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Setter
public class JpaCacheableBasisDefaultPersistentObject implements PersistentObject {

    @Serial
    private static final long serialVersionUID = -8179617300616311221L;

    private OffsetDateTime creationTime;

    private Long founder;

    private Long modifier;

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
