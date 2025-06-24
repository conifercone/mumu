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

package baby.mumu.unique.application.barcode.executor;

import baby.mumu.unique.client.cmds.BarCodeGenerateCmd;
import baby.mumu.unique.domain.barcode.gateway.BarCodeGateway;
import baby.mumu.unique.infra.barcode.convertor.BarCodeConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 条形码生成指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Component
public class BarCodeGenerateCmdExe {

  private final BarCodeGateway barCodeGateway;
  private final BarCodeConvertor barCodeConvertor;

  @Autowired
  public BarCodeGenerateCmdExe(BarCodeGateway barCodeGateway, BarCodeConvertor barCodeConvertor) {
    this.barCodeGateway = barCodeGateway;
    this.barCodeConvertor = barCodeConvertor;
  }

  public byte[] execute(BarCodeGenerateCmd barCodeGenerateCmd) {
    Assert.notNull(barCodeGenerateCmd, "BarCodeGenerateCmd must not be null");
    return barCodeConvertor.toEntity(barCodeGenerateCmd)
      .map(barCodeGateway::generate).orElse(new byte[0]);
  }
}
