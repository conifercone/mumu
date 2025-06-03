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

import io.grpc.Attributes;
import io.grpc.Context;
import io.grpc.Context.Key;
import io.grpc.Contexts;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端IP拦截器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
public class ClientIpInterceptor implements ServerInterceptor {

  // 使用 Context.Key 存储客户端 IP
  private static final Key<String> CLIENT_IP_KEY = Context.key("client-ip");

  @Override
  public <Q, P> Listener<Q> interceptCall(@NotNull ServerCall<Q, P> call,
    Metadata headers,
    ServerCallHandler<Q, P> next) {
    Attributes attributes = call.getAttributes();
    String clientIp = Optional.ofNullable(attributes.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR))
      .filter(socketAddress -> socketAddress instanceof InetSocketAddress)
      .map(socketAddress -> ((InetSocketAddress) socketAddress).getAddress().getHostAddress())
      .orElse(
        StringUtils.EMPTY);
    Context context = Context.current().withValue(ClientIpInterceptor.CLIENT_IP_KEY, clientIp);
    return Contexts.interceptCall(context, call, headers, next);
  }

  public static String getClientIp() {
    return ClientIpInterceptor.CLIENT_IP_KEY.get();
  }
}
