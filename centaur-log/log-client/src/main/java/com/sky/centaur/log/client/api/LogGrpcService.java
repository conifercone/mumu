/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.log.client.api;

import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.sky.centaur.basis.tools.SpringContextUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.client.ServiceInstance;

/**
 * 日志grpc服务
 *
 * @author kaiyu.shan
 * @since 2024-01-31
 */
class LogGrpcService {

  @Resource
  NacosDiscoveryClient nacosDiscoveryClient;

  protected Optional<ManagedChannel> getManagedChannelUsePlaintext() {
    //noinspection DuplicatedCode
    return getServiceInstance().map(
        serviceInstance -> {
          ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(
                  serviceInstance.getHost(),
                  serviceInstance.getPort() + 2)
              .usePlaintext();
          ObservationGrpcClientInterceptor interceptor = null;
          try {
            interceptor = SpringContextUtil.getBean(ObservationGrpcClientInterceptor.class);
          } catch (NoSuchBeanDefinitionException e) {
            // ignore
          }
          if (interceptor != null) {
            builder.intercept(interceptor);
          }
          return builder.build();
        });
  }

  protected Optional<ServiceInstance> getServiceInstance() {
    List<ServiceInstance> instances = nacosDiscoveryClient.getInstances("log");
    return Optional.ofNullable(instances).flatMap(is -> is.stream().findFirst());
  }

}
