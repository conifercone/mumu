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

package baby.mumu.unique.infra.barcode.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.unique.domain.barcode.BarCode;
import baby.mumu.unique.domain.barcode.gateway.BarCodeGateway;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 条形码领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Component
public class BarCodeGatewayImpl implements BarCodeGateway {

  @Override
  public byte[] generate(BarCode barCode) {
    return Optional.ofNullable(barCode).map(barCodeModel -> {
      HashMap<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
      hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
      Code128Writer code128Writer = new Code128Writer();
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        BitMatrix bitMatrix = code128Writer.encode(barCodeModel.getContent(),
          BarcodeFormat.CODE_128,
          barCodeModel.getWidth(), barCodeModel.getHeight(), hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        BufferedImage finalImage = this.insertWords(bufferedImage, barCode);
        Assert.notNull(finalImage, "BufferedImage is null");
        ImageIO.write(finalImage, barCode.getImageFormat().getExtension(), os);
        return os.toByteArray();
      } catch (IOException e) {
        throw new MuMuException(ResponseCode.BARCODE_GENERATION_FAILED);
      }
    }).orElse(new byte[0]);
  }

  private @Nullable BufferedImage insertWords(BufferedImage image, @NotNull BarCode barCode) {
    if (StringUtils.isNotBlank(barCode.getFootContent())) {
      BufferedImage outImage = new BufferedImage(barCode.getWidth(), barCode.getHeight() + 20,
        BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = outImage.createGraphics();
      // 抗锯齿
      this.setGraphics2D(g2d);
      // 设置白色
      this.setColorWhite(g2d, barCode);
      // 画条形码到新的面板
      g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
      // 画文字到新的面板
      Color color = new Color(0, 0, 0);
      g2d.setColor(color);
      // 字体、字型、字号
      g2d.setFont(new Font("微软雅黑", Font.PLAIN, 16));
      // 文字长度
      int strWidth = g2d.getFontMetrics().stringWidth(barCode.getFootContent());
      // 总长度减去文字长度的一半  （居中显示）
      int wordStartX = (barCode.getWidth() - strWidth) / 2;
      // height + (outImage.getHeight() - height) / 2 + 12
      int wordStartY = barCode.getHeight() + 20;
      g2d.drawString(barCode.getFootContent(), wordStartX, wordStartY);
      g2d.dispose();
      outImage.flush();
      return outImage;
    }
    return image;
  }

  /**
   * 设置 Graphics2D 属性  （抗锯齿）
   *
   * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
   */
  private void setGraphics2D(@NotNull Graphics2D g2d) {
    // 消除画图锯齿
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // 消除文字锯齿
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    g2d.setStroke(s);
  }

  private void setColorWhite(@NotNull Graphics2D g2d, @NotNull BarCode barCode) {
    g2d.setColor(Color.WHITE);
    // 填充整个屏幕
    g2d.fillRect(0, 0, barCode.getWidth(), barCode.getHeight() + 20);
    // 设置笔刷
    g2d.setColor(Color.BLACK);
  }
}
