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
import org.jspecify.annotations.NonNull;

/**
 * TraceId id 过滤器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
public class TraceIdFilter implements Filter {

  private static Tracer tracer;

  public TraceIdFilter(Tracer tracer) {
    TraceIdFilter.tracer = tracer;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, @NonNull FilterChain chain)
    throws IOException, ServletException {
    // 继续处理请求
    chain.doFilter(request, response);
  }

  public static Optional<String> traceId() {
    return Optional.ofNullable(TraceIdFilter.tracer).map(Tracer::currentSpan).map(Span::context)
      .map(
        TraceContext::traceId);
  }
}
