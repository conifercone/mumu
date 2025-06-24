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

package baby.mumu.iam.infrastructure.client.convertor;

import baby.mumu.iam.domain.client.Client;
import baby.mumu.iam.infrastructure.client.gatewayimpl.database.po.ClientPO;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.stereotype.Component;

/**
 * 客户端转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.5.0
 */
@Component
public class ClientConvertor {

  @API(status = Status.STABLE, since = "2.5.0")
  public Optional<Client> toEntity(
    ClientPO clientPO) {
    return Optional.ofNullable(clientPO).map(ClientMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.5.0")
  public Optional<ClientPO> toPO(
    Client client) {
    return Optional.ofNullable(client).map(ClientMapper.INSTANCE::toPO);
  }

  @API(status = Status.STABLE, since = "2.5.0")
  public void toEntity(
    Client clientSource, Client clientTarget) {
    if (clientSource != null && clientTarget != null) {
      ClientMapper.INSTANCE.toEntity(clientSource, clientTarget);
    }
  }
}
