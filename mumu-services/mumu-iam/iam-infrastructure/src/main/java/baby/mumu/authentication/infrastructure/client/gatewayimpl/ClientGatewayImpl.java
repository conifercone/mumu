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

package baby.mumu.authentication.infrastructure.client.gatewayimpl;

import baby.mumu.authentication.domain.client.gateway.ClientGateway;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

/**
 * 客户端领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.5.0
 */
@Component
@Observed(name = "ClientGatewayImpl")
public class ClientGatewayImpl implements ClientGateway {

}
