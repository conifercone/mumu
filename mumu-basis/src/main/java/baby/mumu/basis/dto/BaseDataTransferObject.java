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
package baby.mumu.basis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.io.Serial;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Base Data Transfer Object
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public abstract class BaseDataTransferObject implements DataTransferObject {

  @Serial
  private static final long serialVersionUID = 4685385454388931324L;

  /**
   * 创建时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  @CsvBindByName(column = "Creation Time")
  @CsvDate("yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime creationTime;

  /**
   * 创建人
   */
  @CsvBindByName(column = "Founder")
  private Long founder;

  /**
   * 修改人
   */
  @CsvBindByName(column = "Modifier")
  private Long modifier;

  /**
   * 修改时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  @CsvBindByName(column = "Modification Time")
  @CsvDate("yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime modificationTime;

  /**
   * 已归档
   */
  private Boolean archived;

  public OffsetDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(OffsetDateTime creationTime) {
    this.creationTime = creationTime;
  }

  public Long getFounder() {
    return founder;
  }

  public void setFounder(Long founder) {
    this.founder = founder;
  }

  public Long getModifier() {
    return modifier;
  }

  public void setModifier(Long modifier) {
    this.modifier = modifier;
  }

  public OffsetDateTime getModificationTime() {
    return modificationTime;
  }

  public void setModificationTime(OffsetDateTime modificationTime) {
    this.modificationTime = modificationTime;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }
}