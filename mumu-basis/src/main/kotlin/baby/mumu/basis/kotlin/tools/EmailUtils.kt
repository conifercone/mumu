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

package baby.mumu.basis.kotlin.tools

import org.apache.commons.validator.routines.EmailValidator
import org.apiguardian.api.API

/**
 * 邮箱工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.7.0
 */
object EmailUtils {

    /**
     * 校验邮箱地址格式的方法
     * @param email 邮箱地址
     * @return 是否为合法格式的邮箱地址
     */
    @API(status = API.Status.STABLE, since = "2.7.0")
    @JvmStatic
    fun isValidEmailFormat(email: String?): Boolean {
        if (email.isNullOrEmpty()) {
            return false
        }
        return EmailValidator.getInstance().isValid(email)
    }
}
