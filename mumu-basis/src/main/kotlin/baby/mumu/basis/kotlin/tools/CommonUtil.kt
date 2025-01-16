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

import baby.mumu.basis.dto.BaseDataTransferObject
import baby.mumu.basis.exception.MuMuException
import baby.mumu.basis.response.ResponseCode
import org.apiguardian.api.API
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(CommonUtil::class.java)


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
        } catch (e: AddressException) {
            logger.error(e.message, e)
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
     * @param baseDataTransferObject 基础数据传输对象
     */
    @API(status = API.Status.STABLE, since = "1.0.3")
    @JvmStatic
    fun convertToAccountZone(
        baseDataTransferObject: BaseDataTransferObject
    ) {
        Optional.ofNullable(baseDataTransferObject).ifPresent { dataTransferObject ->
            SecurityContextUtil.loginAccountTimezone.ifPresent { timezone ->
                val targetZoneId = ZoneId.of(timezone)
                Optional.ofNullable(dataTransferObject.creationTime).ifPresent {
                    val creationTimeZonedDateTime =
                        dataTransferObject.creationTime.atZoneSameInstant(targetZoneId)
                    dataTransferObject.creationTime = creationTimeZonedDateTime.toOffsetDateTime()
                }
                Optional.ofNullable(dataTransferObject.modificationTime).ifPresent {
                    val modificationTimeZonedDateTime =
                        dataTransferObject.modificationTime.atZoneSameInstant(targetZoneId)
                    dataTransferObject.modificationTime =
                        modificationTimeZonedDateTime.toOffsetDateTime()
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

    @API(status = API.Status.STABLE, since = "2.4.0")
    @JvmStatic
    fun validateTimezone(timezone: String) {
        if (timezone.isNotBlank()) {
            try {
                ZoneId.of(timezone)
            } catch (e: Exception) {
                throw MuMuException(ResponseCode.TIME_ZONE_IS_NOT_AVAILABLE, e)
            }
        }
    }
}
