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
import org.apiguardian.api.API
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

/**
 * 时间工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.7.0
 */
@API(status = API.Status.INTERNAL, since = "2.7.0")
object TimeUtils {

    /**
     * 转换为utc时间
     *
     * @param localDateTime 时间
     * @param zoneId 源时区
     * @return UTC时区时间
     */
    @API(status = API.Status.INTERNAL, since = "2.7.0")
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
    @API(status = API.Status.INTERNAL, since = "2.7.0")
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
     * 转换为账号时区
     *
     * @param localDateTime 时间
     * @param fromZone 源时区
     * @return 账号时区时间
     */
    @API(status = API.Status.INTERNAL, since = "2.7.0")
    @JvmStatic
    fun convertToAccountZone(
        localDateTime: LocalDateTime,
        fromZone: ZoneId
    ): LocalDateTime {
        return SecurityContextUtils.loginAccountTimezone.map { timezone ->
            convertToZone(
                localDateTime,
                fromZone,
                ZoneId.of(timezone)
            )
        }.orElse(localDateTime)
    }

    /**
     * 转换为账号时区
     *
     * @param offsetDateTime 时间
     * @return 账号时区时间
     */
    @API(status = API.Status.INTERNAL, since = "2.7.0")
    @JvmStatic
    fun convertToAccountZone(
        offsetDateTime: OffsetDateTime
    ): OffsetDateTime {
        return SecurityContextUtils.loginAccountTimezone.map { timezone ->
            offsetDateTime.atZoneSameInstant(ZoneId.of(timezone)).toOffsetDateTime()
        }.orElse(offsetDateTime)
    }

    /**
     * 转换为账号时区
     *
     * @param baseDataTransferObject 基础数据传输对象
     */
    @API(status = API.Status.INTERNAL, since = "2.7.0")
    @JvmStatic
    fun convertToAccountZone(
        baseDataTransferObject: BaseDataTransferObject
    ) {
        Optional.ofNullable(baseDataTransferObject).ifPresent { dataTransferObject ->
            SecurityContextUtils.loginAccountTimezone.ifPresent { timezone ->
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
     * UTC时区转换为账号时区
     *
     * @param localDateTime 时间
     * @return 账号时区时间
     */
    @API(status = API.Status.INTERNAL, since = "2.7.0")
    @JvmStatic
    fun convertUTCToAccountZone(
        localDateTime: LocalDateTime
    ): LocalDateTime {
        return convertToAccountZone(localDateTime, ZoneOffset.UTC)
    }

    /**
     * 账号时区转换为UTC时区
     *
     * @param localDateTime 时间
     * @return UTC时区时间
     */
    @API(status = API.Status.INTERNAL, since = "2.7.0")
    @JvmStatic
    fun convertAccountZoneToUTC(
        localDateTime: LocalDateTime
    ): LocalDateTime {
        return SecurityContextUtils.loginAccountTimezone.map { timezone ->
            convertToUTC(
                localDateTime,
                ZoneId.of(timezone)
            )
        }.orElse(localDateTime)
    }

    /**
     * 是否为有效时区类型
     *
     * @param zoneId 时区ID
     */
    @API(status = API.Status.INTERNAL, since = "2.7.0")
    @JvmStatic
    fun isValidTimeZone(zoneId: String): Boolean {
        return ZoneId.getAvailableZoneIds().contains(zoneId)
    }
}
