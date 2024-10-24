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
package baby.mumu.extension;

import java.time.temporal.ChronoUnit;
import lombok.Data;

/**
 * 全局配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
@Data
public class GlobalProperties {

  /**
   * 归档删除期限
   */
  private Long archiveDeletionPeriod = 30L;

  /**
   * 归档删除期限单位
   */
  private ChronoUnit archiveDeletionPeriodUnit = ChronoUnit.DAYS;
}
