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

package baby.mumu.storage.infra.zone.gatewayimpl;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.domain.zone.StorageZone;
import baby.mumu.storage.domain.zone.gateway.StorageZoneGateway;
import baby.mumu.storage.infra.zone.convertor.StorageZoneConvertor;
import baby.mumu.storage.infra.zone.gatewayimpl.database.StorageZoneRepository;
import baby.mumu.storage.infra.zone.gatewayimpl.database.po.StorageZonePO;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 存储区域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@Component
@Observed(name = "StorageZoneGatewayImpl")
public class StorageZoneGatewayImpl implements StorageZoneGateway {

  private final StorageZoneConvertor storageZoneConvertor;
  private final StorageZoneRepository storageZoneRepository;

  public StorageZoneGatewayImpl(StorageZoneConvertor storageZoneConvertor,
    StorageZoneRepository storageZoneRepository) {
    this.storageZoneConvertor = storageZoneConvertor;
    this.storageZoneRepository = storageZoneRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long add(StorageZone storageZone) {
    StorageZonePO storageZonePO = storageZoneConvertor.toStorageZonePO(storageZone)
      .orElseThrow(() -> new ApplicationException(
        ResponseCode.STORAGE_ZONE_INVALID));
    StorageZonePO persisted = storageZoneRepository.persist(storageZonePO);
    return persisted.getId();
  }
}
