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

package baby.mumu.extension.grpc.interceptors;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.util.function.Supplier;

/**
 * token拦截器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.14.0
 */
@SuppressWarnings("ClassCanBeRecord")
public class SafeBearerTokenInterceptor implements ClientInterceptor {

  private static final Metadata.Key<String> AUTH =
    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

  private final Supplier<String> tokenSupplier;

  public SafeBearerTokenInterceptor(Supplier<String> tokenSupplier) {
    this.tokenSupplier = tokenSupplier;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
    MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

    return new ForwardingClientCall.SimpleForwardingClientCall<>(
      next.newCall(method, callOptions)) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        String token = tokenSupplier.get();
        // 只有在 token 非空、非空白时才加头
        if (token != null && !token.isBlank()) {
          headers.put(SafeBearerTokenInterceptor.AUTH, "Bearer " + token);
        }
        super.start(responseListener, headers);
      }
    };
  }
}
