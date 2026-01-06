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

package baby.mumu.basis.provider;

import jakarta.validation.constraints.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * 限流基本信息自动生成默认提供者接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
public class RateLimitingCustomGenerateDefaultProviderImpl implements
    RateLimitingCustomGenerateProvider {

    @Override
    public @NotNull RateLimitingCustomGenerate generate() {
        return new RateLimitingCustomGenerate(5, 1, TimeUnit.SECONDS);
    }
}
