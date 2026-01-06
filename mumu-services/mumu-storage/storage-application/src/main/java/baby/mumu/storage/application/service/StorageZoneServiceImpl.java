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

package baby.mumu.storage.application.service;

import baby.mumu.storage.application.zone.executor.StorageZoneAddCmdExe;
import baby.mumu.storage.client.api.StorageZoneService;
import baby.mumu.storage.client.cmds.StorageZoneAddCmd;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 存储区域管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@Service
@Observed(name = "StorageZoneServiceImpl")
public class StorageZoneServiceImpl implements StorageZoneService {

    private final StorageZoneAddCmdExe storageZoneAddCmdExe;

    public StorageZoneServiceImpl(StorageZoneAddCmdExe storageZoneAddCmdExe) {
        this.storageZoneAddCmdExe = storageZoneAddCmdExe;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(StorageZoneAddCmd storageZoneAddCmd) {
        return storageZoneAddCmdExe.execute(storageZoneAddCmd);
    }
}
