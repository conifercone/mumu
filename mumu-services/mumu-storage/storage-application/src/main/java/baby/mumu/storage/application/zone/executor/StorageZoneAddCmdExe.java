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

package baby.mumu.storage.application.zone.executor;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.client.cmds.StorageZoneAddCmd;
import baby.mumu.storage.domain.zone.StorageZone;
import baby.mumu.storage.domain.zone.gateway.StorageZoneGateway;
import baby.mumu.storage.infra.zone.convertor.StorageZoneConvertor;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

/**
 * 存储区域新增
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@Component
@Observed(name = "StorageZoneAddCmdExe")
public class StorageZoneAddCmdExe {


  private final StorageZoneConvertor storageZoneConvertor;
  private final StorageZoneGateway storageZoneGateway;

  public StorageZoneAddCmdExe(StorageZoneConvertor storageZoneConvertor,
    StorageZoneGateway storageZoneGateway) {
    this.storageZoneConvertor = storageZoneConvertor;
    this.storageZoneGateway = storageZoneGateway;
  }

  public Long execute(StorageZoneAddCmd storageZoneAddCmd) {
    StorageZone storageZone = storageZoneConvertor.toEntity(storageZoneAddCmd)
      .orElseThrow(() -> new ApplicationException(ResponseCode.STORAGE_ZONE_INVALID));
    return storageZoneGateway.add(storageZone);
  }
}
