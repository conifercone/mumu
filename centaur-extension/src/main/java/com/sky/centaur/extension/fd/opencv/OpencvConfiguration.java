/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.extension.fd.opencv;

import com.sky.centaur.extension.ExtensionProperties;
import com.sky.centaur.extension.fd.FaceDetectionProcessor;
import java.io.IOException;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * opencv配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
@ConditionalOnProperty(prefix = "centaur.extension.fd.opencv", value = "enabled", havingValue = "true")
@ConditionalOnClass(CascadeClassifier.class)
public class OpencvConfiguration {

  @Bean
  public CascadeClassifier cascadeClassifier(ResourceLoader resourceLoader) throws IOException {
    Resource resource = resourceLoader.getResource("classpath:haarcascade_frontalface_alt.xml");
    return new CascadeClassifier(resource.getFile().getAbsolutePath());
  }

  @Bean
  @ConditionalOnBean(CascadeClassifier.class)
  @ConditionalOnMissingBean(FaceDetectionProcessor.class)
  public FaceDetectionProcessor faceDetectionProcessor(CascadeClassifier cascadeClassifier) {
    return new OpencvFaceDetectionProcessor(cascadeClassifier);
  }
}
