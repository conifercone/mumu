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

package baby.mumu.build.enums;


import lombok.Getter;

/**
 * 组件枚举
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.11.0
 */
@Getter
public enum ModuleEnum {

  MUMU_BASIS("mumu-basis", "Basic module"),

  MUMU_BENCHMARK("mumu-benchmark", "Benchmark module"),

  MUMU_EXTENSION("mumu-extension", "Function expansion module"),

  MUMU_PROCESSOR("mumu-processor", "Annotation processor"),

  MUMU_IAM("mumu-iam", "Identity & Access Management Service"),

  MUMU_LOG("mumu-log", "Log Management Service"),

  MUMU_MAIL("mumu-mail", "Mail Management Service"),

  MUMU_MESSAGE("mumu-message", "Message Management Service"),

  MUMU_SMS("mumu-sms", "SMS Management Service"),

  MUMU_STORAGE("mumu-storage", "Storage Management Service"),

  MUMU_UNIQUE("mumu-unique", "Unique Management Service");

  private final String description;
  private final String moduleName;

  ModuleEnum(String moduleName, String description) {
    this.description = description;
    this.moduleName = moduleName;
  }
}
