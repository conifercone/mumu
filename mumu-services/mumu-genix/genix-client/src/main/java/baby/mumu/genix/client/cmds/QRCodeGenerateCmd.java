/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.genix.client.cmds;

import baby.mumu.basis.enums.ImageFormatEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

/**
 * 二维码生成指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Data
public class QRCodeGenerateCmd {


    @Schema(description = "二维码内容", requiredMode = RequiredMode.NOT_REQUIRED)
    private String content;

    @Schema(description = "图片宽度", requiredMode = RequiredMode.NOT_REQUIRED)
    private int width = 300;

    @Schema(description = "图片高度", requiredMode = RequiredMode.NOT_REQUIRED)
    private int height = 300;

    @Schema(description = "图片格式", requiredMode = RequiredMode.NOT_REQUIRED)
    private ImageFormatEnum imageFormat = ImageFormatEnum.PNG;
}
