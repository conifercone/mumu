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
package com.sky.centaur.authentication.client.dto.co;

import com.sky.centaur.basis.client.dto.co.BaseClientObject;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色添加客户端对象
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleAddCo extends BaseClientObject {

  private Long id;

  private String name;

  private String code;

  private List<Long> authorities;
}
