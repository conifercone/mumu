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
package com.sky.centaur.basis.kotlin.tools

import org.apiguardian.api.API
import java.util.regex.Pattern
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

/**
 * 通用工具类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
object CommonUtil {
    private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    private val EMAIL_PATTERN: Pattern = Pattern.compile(EMAIL_REGEX)

    /**
     * 校验邮箱地址格式的方法
     * @param email 邮箱地址
     * @return 是否为合法格式的邮箱地址
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun isValidEmailFormat(email: String?): Boolean {
        if (email == null) {
            return false
        }
        val matcher = EMAIL_PATTERN.matcher(email)
        return matcher.matches()
    }

    /**
     * 校验邮箱有效性
     *
     * @param email 邮箱地址
     * @return 是否为有效邮箱地址
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun isValidEmail(email: String?): Boolean {
        if (!isValidEmailFormat(email)) {
            return false
        }
        try {
            val emailAddr = InternetAddress(email)
            emailAddr.validate()
        } catch (ex: AddressException) {
            return false
        }
        return true
    }
}
