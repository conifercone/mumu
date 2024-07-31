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
package com.sky.centaur.extension.translation;

import com.sky.centaur.extension.translation.aliyun.AliyunTranslationConfiguration;
import com.sky.centaur.extension.translation.deepl.DeeplTranslationConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 机器翻译配置类
 *
 * @author kaiyu.shan
 * @since 1.0.3
 */
@Configuration
@ConditionalOnProperty(prefix = "centaur.extension.translation", value = "enabled", havingValue = "true")
@Import(value = {AliyunTranslationConfiguration.class, DeeplTranslationConfiguration.class})
public class TranslationConfiguration {

}
