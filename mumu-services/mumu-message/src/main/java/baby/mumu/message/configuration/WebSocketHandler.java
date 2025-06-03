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

package baby.mumu.message.configuration;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.message.infrastructure.config.MessageProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;


/**
 * 网络套接字处理程序
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

  private final ObjectMapper objectMapper = new ObjectMapper();


  private final MessageProperties messageProperties;

  public static final String SENDER_ACCOUNT_ID = "senderAccountId";
  public static final String RECEIVER_ACCOUNT_ID = "receiverAccountId";

  public WebSocketHandler(MessageProperties messageProperties) {
    this.messageProperties = messageProperties;
  }

  @Override
  public void handlerAdded(@NotNull ChannelHandlerContext ctx) {
    // 添加到channelGroup 通道组
    messageProperties.getWebSocket().getChannelGroup().add(ctx.channel());
  }

  /**
   * 读取数据
   */
  @Override
  protected void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull TextWebSocketFrame msg) {
    Optional.ofNullable(msg.text()).ifPresent(messageText -> {
      try {
        JsonNode messageTextJsonNode = objectMapper.readTree(messageText);
        long receiverAccountId = messageTextJsonNode.get(RECEIVER_ACCOUNT_ID)
          .longValue();
        Optional.ofNullable(messageTextJsonNode.get(SENDER_ACCOUNT_ID))
          .ifPresentOrElse(senderAccountStringId -> {
            long senderAccountId = senderAccountStringId.longValue();
            ConcurrentHashMap<Long, Channel> longChannelConcurrentHashMap = messageProperties.getWebSocket()
              .getAccountSubscriptionChannelMap()
              .computeIfAbsent(receiverAccountId, _ -> new ConcurrentHashMap<>());
            longChannelConcurrentHashMap.computeIfAbsent(senderAccountId, _ -> ctx.channel());
            // 将账号ID作为自定义属性加入到channel中，方便随时channel中获取账号ID
            AttributeKey<String> accountIdKey = AttributeKey.valueOf(SENDER_ACCOUNT_ID);
            ctx.channel().attr(accountIdKey).setIfAbsent(String.valueOf(senderAccountId));
            AttributeKey<String> receiverAccountIdKey = AttributeKey.valueOf(RECEIVER_ACCOUNT_ID);
            ctx.channel().attr(receiverAccountIdKey)
              .setIfAbsent(String.valueOf(receiverAccountId));
          }, () -> {
            messageProperties.getWebSocket()
              .getAccountBroadcastChannelMap()
              .computeIfAbsent(receiverAccountId, _ -> ctx.channel());
            // 将账号ID作为自定义属性加入到channel中，方便随时channel中获取账号ID
            AttributeKey<String> receiverAccountIdKey = AttributeKey.valueOf(RECEIVER_ACCOUNT_ID);
            ctx.channel().attr(receiverAccountIdKey)
              .setIfAbsent(String.valueOf(receiverAccountId));
          });
      } catch (Exception e) {
        throw new MuMuException(ResponseCode.WEBSOCKET_SERVER_CONNECTION_FAILED);
      }
    });
  }

  @Override
  public void handlerRemoved(@NotNull ChannelHandlerContext ctx) {
    messageProperties.getWebSocket().getChannelGroup().remove(ctx.channel());
    removeAccountId(ctx);
  }

  @Override
  public void exceptionCaught(@NotNull ChannelHandlerContext ctx, @NotNull Throwable cause) {
    messageProperties.getWebSocket().getChannelGroup().remove(ctx.channel());
    removeAccountId(ctx);
    ctx.close();
  }

  private void removeAccountId(@NotNull ChannelHandlerContext ctx) {
    AttributeKey<String> senderAccountIdKey = AttributeKey.valueOf(SENDER_ACCOUNT_ID);
    AttributeKey<String> receiverAccountIdKey = AttributeKey.valueOf(RECEIVER_ACCOUNT_ID);
    Optional.ofNullable(ctx.channel().attr(receiverAccountIdKey).get())
      .ifPresent(
        receiverAccountId -> Optional.ofNullable(ctx.channel().attr(senderAccountIdKey).get())
          .ifPresentOrElse(
            senderAccountId -> messageProperties.getWebSocket()
              .getAccountSubscriptionChannelMap()
              .get(Long.parseLong(receiverAccountId))
              .remove(Long.parseLong(senderAccountId)),
            () -> messageProperties.getWebSocket().getAccountBroadcastChannelMap()
              .remove(Long.parseLong(receiverAccountId))));
  }
}
