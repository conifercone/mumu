/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.genix.application.service;

import baby.mumu.genix.application.barcode.executor.BarCodeGenerateCmdExe;
import baby.mumu.genix.client.api.BarCodeService;
import baby.mumu.genix.client.cmds.BarCodeGenerateCmd;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

/**
 * 条形码service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Service
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
