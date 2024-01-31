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
package com.sky.centaur.extension.processor.grpc;

import com.sky.centaur.extension.exception.CentaurException;
import io.grpc.Status;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;

/**
 * grpc异常统一处理
 *
 * @author 单开宇
 * @since 2024-01-31
 */
@GRpcServiceAdvice
public class GrpcExceptionAdvice {

  @SuppressWarnings("unused")
  @GRpcExceptionHandler
  public Status handle(CentaurException centaurException, GRpcExceptionScope gRpcExceptionScope) {
    return Status.INTERNAL;
  }
}
