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

package baby.mumu.basis.mappers;

import baby.mumu.basis.dto.BaseDataTransferObject;
import baby.mumu.basis.kotlin.tools.TimeUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * client object mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
public interface DataTransferObjectMapper {

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget BaseDataTransferObject baseDataTransferObject) {
    TimeUtils.convertToAccountZone(baseDataTransferObject);
  }
}
