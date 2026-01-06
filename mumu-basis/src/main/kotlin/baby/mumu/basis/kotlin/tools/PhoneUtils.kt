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

package baby.mumu.basis.kotlin.tools

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import org.apiguardian.api.API

/**
 * 手机号工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.7.0
 */
object PhoneUtils {

    private val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    /**
     * 验证手机号格式
     * @param phoneNumber 手机号
     * @param countryCode 国家区号（如 "+86"）
     * @return Boolean 是否有效
     */
    @API(status = API.Status.STABLE, since = "2.7.0")
    @JvmStatic
    fun isValidPhoneNumber(phoneNumber: String?, countryCode: String?): Boolean {
        if (phoneNumber.isNullOrBlank() || countryCode.isNullOrBlank()) return false
        return try {
            val countryCodeInt = countryCode.replace("+", "").toInt()
            val regionCode =
                phoneNumberUtil.getRegionCodeForCountryCode(countryCodeInt) ?: return false
            val parsedNumber: PhoneNumber =
                phoneNumberUtil.parse(phoneNumber, regionCode)
            phoneNumberUtil.isValidNumber(parsedNumber)
        } catch (_: NumberParseException) {
            false
        } catch (_: NumberFormatException) {
            false
        }
    }
}
