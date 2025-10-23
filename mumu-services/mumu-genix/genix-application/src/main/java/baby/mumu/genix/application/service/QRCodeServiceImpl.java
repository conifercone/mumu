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

package baby.mumu.genix.application.service;

import baby.mumu.genix.application.qrcode.executor.QRCodeGenerateCmdExe;
import baby.mumu.genix.client.api.QRCodeService;
import baby.mumu.genix.client.cmds.QRCodeGenerateCmd;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

/**
 * 二维码service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Service
@Observed(name = "QRCodeServiceImpl")
public class QRCodeServiceImpl implements QRCodeService {

  private final QRCodeGenerateCmdExe qrCodeGenerateCmdExe;

  public QRCodeServiceImpl(QRCodeGenerateCmdExe qrCodeGenerateCmdExe) {
    this.qrCodeGenerateCmdExe = qrCodeGenerateCmdExe;
  }

  @Override
  public byte[] generate(QRCodeGenerateCmd qrCodeGenerateCmd) {
    return qrCodeGenerateCmdExe.execute(qrCodeGenerateCmd);
  }
}
