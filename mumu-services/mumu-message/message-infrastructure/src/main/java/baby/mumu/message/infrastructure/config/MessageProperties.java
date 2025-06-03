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

package baby.mumu.message.infrastructure.config;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 消息服务全局配置信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Data
@ConfigurationProperties("mumu.message")
public class MessageProperties {

  @NestedConfigurationProperty
  private WebSocket webSocket = new WebSocket();


  @Data
  public static class WebSocket {

    /**
     * 定义一个channel组，管理所有的channel GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    @Getter
    private final ChannelGroup channelGroup = new DefaultChannelGroup(
      GlobalEventExecutor.INSTANCE);
    /**
     * 存放账号与Chanel的对应信息，用于给指定账号发送订阅消息 key: 接收方ID value: Map: key: 发送者ID value: Channel
     */
    @Getter
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Channel>> accountSubscriptionChannelMap = new ConcurrentHashMap<>();

    /**
     * 存放账号与Chanel的对应信息，用于给指定账号发送广播消息 key: 接收方ID value: Channel
     */
    @Getter
    private final ConcurrentHashMap<Long, Channel> accountBroadcastChannelMap = new ConcurrentHashMap<>();

    /**
     * 路径
     */
    private String path = "/webSocket";

    /**
     * 端口
     */
    private int port = 31704;
  }

}
