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

package com.sky.centaur.basis.enums;

import lombok.Getter;

/**
 * oauth2常量枚举
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Getter
public enum OAuth2Enum {
  GRANT_TYPE_PASSWORD("authorization_password", "密码模式");

  private final String name;

  private final String description;


  OAuth2Enum(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
