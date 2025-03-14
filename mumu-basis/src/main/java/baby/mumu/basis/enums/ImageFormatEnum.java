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
package baby.mumu.basis.enums;

import lombok.Getter;

/**
 * 图片格式枚举
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.4
 */
@Getter
public enum ImageFormatEnum {
  JPEG("jpeg", "image/jpeg"),
  PNG("png", "image/png"),
  GIF("gif", "image/gif"),
  BMP("bmp", "image/bmp"),
  TIFF("tiff", "image/tiff");

  private final String extension;
  private final String mimeType;

  ImageFormatEnum(String extension, String mimeType) {
    this.extension = extension;
    this.mimeType = mimeType;
  }

  @Override
  public String toString() {
    return extension;
  }
}
