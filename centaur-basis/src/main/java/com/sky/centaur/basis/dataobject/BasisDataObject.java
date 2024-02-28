/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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

package com.sky.centaur.basis.dataobject;

import java.time.OffsetDateTime;

/**
 * 基础数据对象
 *
 * @author 单开宇
 * @since 2024-02-26
 */
public abstract class BasisDataObject {

  public abstract Long getFounder();

  public abstract Long getModifier();

  public abstract OffsetDateTime getCreationTime();

  public abstract OffsetDateTime getModificationTime();

  public abstract void setFounder(Long founder);

  public abstract void setModifier(Long modifier);

  public abstract void setCreationTime(OffsetDateTime creationTime);

  public abstract void setModificationTime(OffsetDateTime modificationTime);
}