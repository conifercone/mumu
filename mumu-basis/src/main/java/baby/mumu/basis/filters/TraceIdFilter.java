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
package baby.mumu.basis.filters;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.NamedInheritableThreadLocal;

/**
 * TraceId id 过滤器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
public class TraceIdFilter implements Filter {

  // InheritableThreadLocal 变量用于存储 TraceId
  private static final NamedInheritableThreadLocal<String> traceIdThreadLocal = new NamedInheritableThreadLocal<>(
    "TraceId");

  private final Tracer tracer;

  public TraceIdFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, @NotNull FilterChain chain)
    throws IOException, ServletException {
    try {
      traceId().ifPresent(traceIdThreadLocal::set);
      // 继续处理请求
      chain.doFilter(request, response);
    } finally {
      // 清理 ThreadLocal，避免内存泄漏
      traceIdThreadLocal.remove();
    }
  }

  private Optional<String> traceId() {
    return Optional.ofNullable(tracer).map(Tracer::currentSpan).map(Span::context).map(
      TraceContext::traceId);
  }

  // 静态方法用于获取当前线程的 TraceId
  public static String getTraceId() {
    return traceIdThreadLocal.get();
  }

  public static void setTraceId(String traceId) {
    traceIdThreadLocal.set(traceId);
  }

  public static void removeTraceId() {
    traceIdThreadLocal.remove();
  }
}
