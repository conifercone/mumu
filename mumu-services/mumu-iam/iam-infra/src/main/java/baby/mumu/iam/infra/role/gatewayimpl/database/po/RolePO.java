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

package baby.mumu.iam.infra.role.gatewayimpl.database.po;

import baby.mumu.basis.po.jpa.JpaBasisArchivablePersistentObject;
import baby.mumu.genix.client.config.SnowflakeIdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.util.Objects;

/**
 * 角色基本信息数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Getter
@Setter
@Entity
@Table(name = "mumu_roles")
@DynamicInsert
@ToString
public class RolePO extends JpaBasisArchivablePersistentObject {

    @Serial
    private static final long serialVersionUID = 2601328894471265295L;

    /**
     * 角色id
     */
    @Id
    @SnowflakeIdGenerator
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 角色名
     */
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * 角色编码
     */
    @Size(max = 100)
    @NotNull
    @Column(name = "code", nullable = false, length = 100)
    private String code;

    /**
     * 角色描述
     */
    @Size(max = 500)
    @ColumnDefault("''")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

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
        RolePO rolePO = (RolePO) o;
        return getId() != null && Objects.equals(getId(), rolePO.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
            .getPersistentClass().hashCode() : getClass().hashCode();
    }
}
