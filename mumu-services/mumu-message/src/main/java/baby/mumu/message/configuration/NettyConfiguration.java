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

import baby.mumu.message.infrastructure.config.MessageProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.InetSocketAddress;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * netty配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Configuration
@EnableConfigurationProperties(MessageProperties.class)
public class NettyConfiguration {

  private static final Logger log = LoggerFactory.getLogger(NettyConfiguration.class);
  private static final String WEBSOCKET_PROTOCOL = "WebSocket";
  private EventLoopGroup bossGroup;
  private EventLoopGroup workGroup;
  private final MessageProperties messageProperties;

  @Autowired
  public NettyConfiguration(MessageProperties messageProperties) {
    this.messageProperties = messageProperties;
  }

  @Bean
  public WebSocketHandler webSocketHandler() {
    return new WebSocketHandler(messageProperties);
  }

  private void start() throws InterruptedException {
    bossGroup = new NioEventLoopGroup();
    workGroup = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();
    // bossGroup辅助客户端的tcp连接请求, workGroup负责与客户端之前的读写操作
    bootstrap.group(bossGroup, workGroup);
    // 设置NIO类型的channel
    bootstrap.channel(NioServerSocketChannel.class);
    // 设置监听端口
    bootstrap.localAddress(new InetSocketAddress(messageProperties.getWebSocket().getPort()));
    // 连接到达时会创建一个通道
    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(@NotNull SocketChannel socketChannel) {
        // 流水线管理通道中的处理程序（Handler），用来处理业务
        // webSocket协议本身是基于http协议的，所以这边也要使用http编解码器
        socketChannel.pipeline().addLast(new HttpServerCodec());
        // 以块的方式来写的处理器
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                /*
                说明：
                1、http数据在传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
                2、这就是为什么，当浏览器发送大量数据时，就会发送多次http请求
                 */
        socketChannel.pipeline().addLast(new HttpObjectAggregator(8192));
                /*
                说明：
                1、对应webSocket，它的数据是以帧（frame）的形式传递
                2、浏览器请求时 ws://localhost:58080/xxx 表示请求的uri
                3、核心功能是将http协议升级为ws协议，保持长连接
                */
        socketChannel.pipeline().addLast(
          new WebSocketServerProtocolHandler(messageProperties.getWebSocket().getPath(),
            NettyConfiguration.WEBSOCKET_PROTOCOL, true,
            65536 * 10));
        // 自定义的handler，处理业务逻辑
        socketChannel.pipeline().addLast(webSocketHandler());
      }
    });
    // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
    ChannelFuture channelFuture = bootstrap.bind().sync();
    NettyConfiguration.log.info("Server started and listen on:{}",
      channelFuture.channel().localAddress());
    // 对关闭通道进行监听
    channelFuture.channel().closeFuture().sync();
  }


  @PreDestroy
  public void destroy() throws InterruptedException {
    if (bossGroup != null) {
      bossGroup.shutdownGracefully().sync();
    }
    if (workGroup != null) {
      workGroup.shutdownGracefully().sync();
    }
  }

  @PostConstruct()
  public void init() {
    new Thread(() -> {
      try {
        start();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }).start();
  }
}
