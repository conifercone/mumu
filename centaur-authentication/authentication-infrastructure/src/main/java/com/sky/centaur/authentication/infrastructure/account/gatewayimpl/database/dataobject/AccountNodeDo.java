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

package com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject;

import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleNodeDo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * 账户节点
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Node("account")
@Data
@RequiredArgsConstructor
public class AccountNodeDo {

  @Id
  private Long id;

  @Property("username")
  private String username;

  @Relationship(type = "role", direction = Relationship.Direction.OUTGOING)
  private RoleNodeDo role;

}
