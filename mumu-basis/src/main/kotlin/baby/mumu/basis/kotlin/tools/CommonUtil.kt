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

import baby.mumu.basis.client.dto.co.BaseClientObject
import org.apiguardian.api.API
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import java.util.regex.Pattern
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

/**
 * 通用工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
object CommonUtil {
    private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    private val EMAIL_PATTERN: Pattern = Pattern.compile(EMAIL_REGEX)

    @Suppress("SpellCheckingInspection")
    private const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private val RANDOM = SecureRandom()

    /**
     * 校验邮箱地址格式的方法
     * @param email 邮箱地址
     * @return 是否为合法格式的邮箱地址
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun isValidEmailFormat(email: String?): Boolean {
        if (email.isNullOrEmpty()) {
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

    /**
     * 转换为utc时间
     *
     * @param localDateTime 时间
     * @param zoneId 源时区
     * @return UTC时区时间
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun convertToUTC(localDateTime: LocalDateTime, zoneId: ZoneId): LocalDateTime {
        return convertToZone(localDateTime, zoneId, ZoneOffset.UTC)
    }

    /**
     * 转换为指定时区
     *
     * @param localDateTime 时间
     * @param fromZone 源时区
     * @param toZone 目标时区
     * @return 指定时区时间
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun convertToZone(
        localDateTime: LocalDateTime,
        fromZone: ZoneId,
        toZone: ZoneId
    ): LocalDateTime {
        val zonedDateTime = localDateTime.atZone(fromZone)
        val converted = zonedDateTime.withZoneSameInstant(toZone)
        return converted.toLocalDateTime()
    }

    /**
     * 转换为账户时区
     *
     * @param localDateTime 时间
     * @param fromZone 源时区
     * @return 账户时区时间
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun convertToAccountZone(
        localDateTime: LocalDateTime,
        fromZone: ZoneId
    ): LocalDateTime {
        return SecurityContextUtil.loginAccountTimezone.map { timezone ->
            convertToZone(
                localDateTime,
                fromZone,
                ZoneId.of(timezone)
            )
        }.orElse(localDateTime)
    }

    /**
     * 转换为账户时区
     *
     * @param offsetDateTime 时间
     * @return 账户时区时间
     */
    @API(status = API.Status.STABLE, since = "2.2.0")
    @JvmStatic
    fun convertToAccountZone(
        offsetDateTime: OffsetDateTime
    ): OffsetDateTime {
        return SecurityContextUtil.loginAccountTimezone.map { timezone ->
            offsetDateTime.atZoneSameInstant(ZoneId.of(timezone)).toOffsetDateTime()
        }.orElse(offsetDateTime)
    }

    /**
     * 转换为账户时区
     *
     * @param baseClientObject 客户端对象
     */
    @API(status = API.Status.STABLE, since = "1.0.3")
    @JvmStatic
    fun convertToAccountZone(
        baseClientObject: BaseClientObject
    ) {
        Optional.ofNullable(baseClientObject).ifPresent { baseCo ->
            SecurityContextUtil.loginAccountTimezone.ifPresent { timezone ->
                val targetZoneId = ZoneId.of(timezone)
                Optional.ofNullable(baseCo.creationTime).ifPresent {
                    val creationTimeZonedDateTime =
                        baseCo.creationTime.atZoneSameInstant(targetZoneId)
                    baseCo.creationTime = creationTimeZonedDateTime.toOffsetDateTime()
                }
                Optional.ofNullable(baseCo.modificationTime).ifPresent {
                    val modificationTimeZonedDateTime =
                        baseCo.modificationTime.atZoneSameInstant(targetZoneId)
                    baseCo.modificationTime = modificationTimeZonedDateTime.toOffsetDateTime()
                }
            }
        }
    }

    /**
     * UTC时区转换为账户时区
     *
     * @param localDateTime 时间
     * @return 账户时区时间
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun convertUTCToAccountZone(
        localDateTime: LocalDateTime
    ): LocalDateTime {
        return convertToAccountZone(localDateTime, ZoneOffset.UTC)
    }

    /**
     * 账户时区转换为UTC时区
     *
     * @param localDateTime 时间
     * @return UTC时区时间
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    @JvmStatic
    fun convertAccountZoneToUTC(
        localDateTime: LocalDateTime
    ): LocalDateTime {
        return SecurityContextUtil.loginAccountTimezone.map { timezone ->
            convertToUTC(
                localDateTime,
                ZoneId.of(timezone)
            )
        }.orElse(localDateTime)
    }

    /**
     * 随机生成指定长度的字符串
     *
     * @param length 长度
     * @return 生成结果
     */
    @API(status = API.Status.STABLE, since = "1.0.1")
    @JvmStatic
    fun generateRandomString(length: Int): String {
        require(length > 0) { "Length must be a positive number." }

        val stringBuilder = StringBuilder(length)
        repeat(length) {
            stringBuilder.append(CHARACTERS[RANDOM.nextInt(CHARACTERS.length)])
        }
        return stringBuilder.toString()
    }
}
