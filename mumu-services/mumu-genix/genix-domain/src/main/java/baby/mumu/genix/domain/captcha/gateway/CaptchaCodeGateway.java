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

package baby.mumu.genix.domain.captcha.gateway;

import baby.mumu.genix.domain.captcha.CaptchaCode;

/**
 * 验证码领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
public interface CaptchaCodeGateway {

    /**
     * 生成指定长度的简单验证码
     *
     * @param captchaCode 简单验证码
     * @return 验证码
     */
    CaptchaCode generate(CaptchaCode captchaCode);


    /**
     * 验证简单验证码
     *
     * @param captchaCode 简单验证码
     * @return 验证结果
     */
    boolean verify(CaptchaCode captchaCode);

    /**
     * 根据ID删除验证码
     *
     * @param captchaCodeId 验证码ID
     */
    void delete(Long captchaCodeId);

}
