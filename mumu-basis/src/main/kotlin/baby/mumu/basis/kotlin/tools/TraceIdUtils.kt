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

import baby.mumu.basis.constants.MDCConstants
import io.micrometer.tracing.Tracer
import org.slf4j.MDC

/**
 * 全局链路ID工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.15.0
 */
object TraceIdUtils {

    @JvmStatic
    fun getTraceId(): String {
        // 先取 MDC
        val mdcId = MDC.get(MDCConstants.TRACE_ID)
        if (!mdcId.isNullOrEmpty()) {
            return mdcId
        }

        // 再尝试取 Tracer
        return try {
            val tracer = SpringContextUtils.getApplicationContext()
                ?.getBeanProvider(Tracer::class.java)
                ?.getIfAvailable()

            val span = tracer?.currentSpan()
            span?.context()?.traceId() ?: ""
        } catch (_: Exception) {
            ""
        }
    }
}
