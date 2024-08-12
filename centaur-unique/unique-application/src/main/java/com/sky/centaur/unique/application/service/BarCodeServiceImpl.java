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
package com.sky.centaur.unique.application.service;

import com.sky.centaur.unique.application.barcode.executor.BarCodeGenerateCmdExe;
import com.sky.centaur.unique.client.api.BarCodeService;
import com.sky.centaur.unique.client.dto.BarCodeGenerateCmd;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Service;

/**
 * 条形码service实现类
 *
 * @author kaiyu.shan
 * @since 1.0.4
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "BarCodeServiceImpl")
public class BarCodeServiceImpl implements BarCodeService {

  private final BarCodeGenerateCmdExe barCodeGenerateCmdExe;

  public BarCodeServiceImpl(BarCodeGenerateCmdExe barCodeGenerateCmdExe) {
    this.barCodeGenerateCmdExe = barCodeGenerateCmdExe;
  }

  @Override
  public byte[] generate(BarCodeGenerateCmd barCodeGenerateCmd) {
    return barCodeGenerateCmdExe.execute(barCodeGenerateCmd);
  }
}