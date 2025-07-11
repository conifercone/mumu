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

package baby.mumu.jmh;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.results.format.ResultFormatType;

/**
 * 基准测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.10.0
 */
public class MuMuBenchmarkUtils {

  public static final String RESULT_FILE_PATH_TEMPLATE = "./benchmark-history/%s.%s";

  public static @NotNull String getResultFilePath(@NotNull Class<?> clazz,
    @NotNull ResultFormatType format) {
    return String.format(MuMuBenchmarkUtils.RESULT_FILE_PATH_TEMPLATE, clazz.getSimpleName(),
      format.toString().toLowerCase());
  }
}
