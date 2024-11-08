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
package baby.mumu.authentication.infrastructure.relations.database;

import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.dataobject.jpa.JpaBasisDefaultDataObject;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

/**
 * 权限路径
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
@Getter
@Setter
@Entity
@Table(name = "authority_paths")
@DynamicInsert
@Metamodel
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityPathsDo extends JpaBasisDefaultDataObject {

  @Serial
  private static final long serialVersionUID = 5664371470283158730L;

  @EmbeddedId
  private AuthorityPathsDoId id;

  @MapsId("ancestorId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ancestor_id", nullable = false)
  private AuthorityDo ancestor;

  @MapsId("descendantId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "descendant_id", nullable = false)
  private AuthorityDo descendant;
}
