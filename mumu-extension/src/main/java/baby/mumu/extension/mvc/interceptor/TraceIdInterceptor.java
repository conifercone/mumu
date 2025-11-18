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

package baby.mumu.extension.mvc.interceptor;

import baby.mumu.basis.constants.MDCConstants;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全局链路追踪ID拦截器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.15.0
 */
public class TraceIdInterceptor implements HandlerInterceptor {

  private static final String TRACE_ID_ATTRIBUTE_NAME = "TRACE_ID";
  private final ObjectProvider<Tracer> tracerProvider;
  private static final String TRACE_ID_HEADER_NAME = "X-Trace-Id";

  public TraceIdInterceptor(ObjectProvider<Tracer> tracerProvider) {
    this.tracerProvider = tracerProvider;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
    Object handler) {
    String traceId = null;
    try {
      Tracer tracer = tracerProvider.getIfAvailable();
      if (tracer != null && tracer.currentSpan() != null) {
        traceId = Objects.requireNonNull(tracer.currentSpan()).context().traceId();
      }
    } catch (Exception ignored) {
    }

    if (traceId == null || traceId.isBlank()) {
      String fromHeader = request.getHeader(TraceIdInterceptor.TRACE_ID_HEADER_NAME);
      if (fromHeader != null && !fromHeader.isBlank()) {
        traceId = fromHeader;
      }
    }

    if (traceId != null) {
      MDC.put(MDCConstants.TRACE_ID, traceId);
      request.setAttribute(TraceIdInterceptor.TRACE_ID_ATTRIBUTE_NAME, traceId);
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
    Object handler, Exception ex) {
    MDC.remove(MDCConstants.TRACE_ID);
  }
}
