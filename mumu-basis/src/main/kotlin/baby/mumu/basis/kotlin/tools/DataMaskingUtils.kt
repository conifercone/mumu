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
package baby.mumu.basis.kotlin.tools

/**
 * 数据脱敏工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
object DataMaskingUtils {

    /**
     * 脱敏手机号码: 保留前3位和后4位，其他部分用*代替
     *
     * @param phoneNumber 手机号码
     * @return 脱敏后的手机号码
     */
    @JvmStatic
    fun maskPhoneNumber(phoneNumber: String?): String? {
        return if (phoneNumber != null && phoneNumber.length == 11) {
            phoneNumber.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
        } else {
            phoneNumber
        }
    }

    /**
     * 脱敏身份证号码: 保留前6位和后4位，其他部分用*代替
     *
     * @param idCard 身份证号码
     * @return 脱敏后的身份证号码
     */
    @JvmStatic
    fun maskIdCard(idCard: String?): String? {
        return if (idCard != null && idCard.length == 18) {
            idCard.replace("(\\d{6})\\d{8}(\\d{4})".toRegex(), "$1********$2")
        } else {
            idCard
        }
    }

    /**
     * 脱敏电子邮箱: 保留前1位和@之后的部分，其他部分用*代替
     *
     * @param email 电子邮箱
     * @return 脱敏后的电子邮箱
     */
    @JvmStatic
    fun maskEmail(email: String?): String? {
        return if (email != null && email.contains("@")) {
            val parts = email.split("@")
            val localPart = parts[0]
            val maskedLocalPart = if (localPart.length > 1) {
                localPart[0] + "*".repeat(localPart.length - 1)
            } else {
                localPart
            }
            "$maskedLocalPart@${parts[1]}"
        } else {
            email
        }
    }

    /**
     * 脱敏姓名: 只保留姓，其他部分用*代替
     *
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    @JvmStatic
    fun maskName(name: String?): String? {
        return if (name != null && name.length >= 2) {
            name[0] + "*".repeat(name.length - 1)
        } else {
            name
        }
    }

    /**
     * 脱敏银行卡号: 保留前4位和后4位，其他部分用*代替
     *
     * @param bankCardNumber 银行卡号
     * @return 脱敏后的银行卡号
     */
    @JvmStatic
    fun maskBankCard(bankCardNumber: String?): String? {
        return if (bankCardNumber != null && bankCardNumber.length >= 8) {
            bankCardNumber.replace("(\\d{4})\\d+(\\d{4})".toRegex(), "$1****$2")
        } else {
            bankCardNumber
        }
    }

    /**
     * 通用脱敏方法: 保留指定前缀和后缀长度，中间部分用*代替
     *
     * @param data 原始数据
     * @param prefixLength 保留的前缀长度
     * @param suffixLength 保留的后缀长度
     * @return 脱敏后的数据
     */
    @JvmStatic
    fun maskData(data: String?, prefixLength: Int, suffixLength: Int): String? {
        return if (data != null && data.length >= prefixLength + suffixLength) {
            val prefix = data.substring(0, prefixLength)
            val suffix = data.substring(data.length - suffixLength)
            prefix + "*".repeat(data.length - prefixLength - suffixLength) + suffix
        } else {
            data
        }
    }
}

