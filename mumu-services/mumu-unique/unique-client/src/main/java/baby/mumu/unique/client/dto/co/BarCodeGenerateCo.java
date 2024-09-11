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
package baby.mumu.unique.client.dto.co;

import baby.mumu.basis.client.dto.co.BaseClientObject;
import baby.mumu.basis.enums.ImageFormatEnum;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 条形码生成客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BarCodeGenerateCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 2255152031273276995L;

  private String content;

  private int width = 300;

  private int height = 50;

  private ImageFormatEnum imageFormat = ImageFormatEnum.PNG;

  private String footContent;
}
