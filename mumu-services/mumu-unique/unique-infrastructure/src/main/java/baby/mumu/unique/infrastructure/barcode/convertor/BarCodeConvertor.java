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

package baby.mumu.unique.infrastructure.barcode.convertor;

import baby.mumu.unique.client.cmds.BarCodeGenerateCmd;
import baby.mumu.unique.domain.barcode.BarCode;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;

/**
 * 条形码对象转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Component
public class BarCodeConvertor {

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<BarCode> toEntity(
    BarCodeGenerateCmd barCodeGenerateCmd) {
    return Optional.ofNullable(barCodeGenerateCmd).map(BarCodeMapper.INSTANCE::toEntity);
  }
}
