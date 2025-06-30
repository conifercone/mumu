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

package baby.mumu.unique.application.qrcode.executor;

import baby.mumu.unique.client.cmds.QRCodeGenerateCmd;
import baby.mumu.unique.domain.qrcode.gateway.QRCodeGateway;
import baby.mumu.unique.infra.qrcode.convertor.QRCodeConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 二维码生成指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Component
public class QRCodeGenerateCmdExe {

  private final QRCodeGateway qrCodeGateway;
  private final QRCodeConvertor qrCodeConvertor;

  @Autowired
  public QRCodeGenerateCmdExe(QRCodeGateway qrCodeGateway, QRCodeConvertor qrCodeConvertor) {
    this.qrCodeGateway = qrCodeGateway;
    this.qrCodeConvertor = qrCodeConvertor;
  }

  public byte[] execute(QRCodeGenerateCmd qrCodeGenerateCmd) {
    Assert.notNull(qrCodeGenerateCmd, "QRCodeGenerateCmd must not be null");
    return qrCodeConvertor.toEntity(qrCodeGenerateCmd)
      .map(qrCodeGateway::generate).orElse(new byte[0]);
  }
}
