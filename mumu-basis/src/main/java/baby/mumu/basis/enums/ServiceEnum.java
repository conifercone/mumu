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

package baby.mumu.basis.enums;

import lombok.Getter;

/**
 * 服务枚举
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.11.0
 */
@Getter
public enum ServiceEnum {

  /**
   * Identity & Access Management Service
   */
  IAM("iam", "Identity & Access Management Service"),

  /**
   * Log Service
   */
  LOG("log", "Log Management Service"),

  MAIL("mail", "Mail Management Service"),

  MESSAGE("message", "Message Management Service"),

  SMS("sms", "SMS Management Service"),

  STORAGE("storage", "Storage Management Service"),

  UNIQUE("unique", "Unique Management Service");

  private final String name;
  private final String description;

  ServiceEnum(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
