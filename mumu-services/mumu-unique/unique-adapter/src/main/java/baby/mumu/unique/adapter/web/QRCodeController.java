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
package baby.mumu.unique.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.unique.client.api.QRCodeService;
import baby.mumu.unique.client.cmds.QRCodeGenerateCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Base64;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 二维码相关接口
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.4
 */
@RestController
@RequestMapping("/qrCode")
@Tag(name = "二维码管理")
public class QRCodeController {

  private final QRCodeService qrCodeService;

  @Autowired
  public QRCodeController(QRCodeService qrCodeService) {
    this.qrCodeService = qrCodeService;
  }

  @Operation(summary = "生成二维码（返回Base64格式的图片数据链接）")
  @GetMapping("/dataUrl")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public ResponseWrapper<String> dataUrlGenerate(
    @ModelAttribute @Valid QRCodeGenerateCmd qrCodeGenerateCmd) {
    return ResponseWrapper.success(String.format(CommonConstants.DATA_URL_TEMPLATE,
      qrCodeGenerateCmd.getImageFormat().getMimeType(),
      Base64.getEncoder().encodeToString(qrCodeService.generate(qrCodeGenerateCmd))));
  }
}
