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

package baby.mumu.genix.infra.qrcode.gatewayimpl;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.genix.domain.qrcode.QRCode;
import baby.mumu.genix.domain.qrcode.gateway.QRCodeGateway;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 二维码领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Component
public class QRCodeGatewayImpl implements QRCodeGateway {

  @Override
  public byte[] generate(QRCode qrCode) {
    return Optional.ofNullable(qrCode).map(qrCodeModel -> {
      HashMap<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
      hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
      hints.put(EncodeHintType.MARGIN, 1);
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeModel.getContent(), BarcodeFormat.QR_CODE,
          qrCodeModel.getWidth(), qrCodeModel.getHeight(), hints);
        MatrixToImageWriter.writeToStream(bitMatrix, qrCodeModel.getImageFormat().getExtension(),
          os);
        return os.toByteArray();
      } catch (IOException | WriterException e) {
        throw new ApplicationException(ResponseCode.QR_CODE_GENERATION_FAILED);
      }
    }).orElse(new byte[0]);
  }
}
