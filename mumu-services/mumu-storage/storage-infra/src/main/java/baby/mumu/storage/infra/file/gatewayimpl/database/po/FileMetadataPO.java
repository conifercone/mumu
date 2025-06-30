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

package baby.mumu.storage.infra.file.gatewayimpl.database.po;

import baby.mumu.basis.po.jpa.JpaBasisDefaultPersistentObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.proxy.HibernateProxy;

/**
 * 文件元数据基本信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@Entity
@Table(name = "mumu_file_metadata")
@Getter
@Setter
@RequiredArgsConstructor
@DynamicInsert
@ToString
public class FileMetadataPO extends JpaBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = -1394204529891009898L;

  /**
   * 文件元数据id
   */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 255)
  @NotNull
  @Column(name = "original_filename", nullable = false)
  private String originalFilename;

  @Size(max = 255)
  @NotNull
  @Column(name = "stored_filename", nullable = false)
  private String storedFilename;

  @Size(max = 255)
  @NotNull
  @Column(name = "content_type", nullable = false)
  private String contentType;

  @NotNull
  @Column(name = "size", nullable = false)
  private Long size;

  @Size(max = 255)
  @NotNull
  @Column(name = "storage_zone", nullable = false)
  private String storageZone;

  @Size(max = 255)
  @NotNull
  @Column(name = "storage_path", nullable = false)
  private String storagePath;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy
      ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
      : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy
      ? ((HibernateProxy) this).getHibernateLazyInitializer()
      .getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    FileMetadataPO that = (FileMetadataPO) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
      .getPersistentClass().hashCode() : getClass().hashCode();
  }
}
