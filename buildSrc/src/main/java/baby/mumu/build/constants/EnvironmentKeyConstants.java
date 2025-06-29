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

package baby.mumu.build.constants;

import lombok.experimental.UtilityClass;

/**
 * 环境变量键名常量类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.11.0
 */
@UtilityClass
public class EnvironmentKeyConstants {

  public final String MUMU_SIGNING_KEY_ID = "MUMU_SIGNING_KEY_ID";

  public final String MUMU_SIGNING_KEY_FILE_PATH = "MUMU_SIGNING_KEY_FILE_PATH";

  public final String MUMU_SIGNING_KEY_CONTENT = "MUMU_SIGNING_KEY_CONTENT";

  public final String MUMU_SIGNING_PASSWORD = "MUMU_SIGNING_PASSWORD";
}
