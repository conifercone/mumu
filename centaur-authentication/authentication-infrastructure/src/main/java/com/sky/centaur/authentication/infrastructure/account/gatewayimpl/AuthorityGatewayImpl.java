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

package com.sky.centaur.authentication.infrastructure.account.gatewayimpl;

import com.sky.centaur.authentication.domain.account.Authority;
import com.sky.centaur.authentication.domain.account.gateway.AuthorityGateway;
import com.sky.centaur.authentication.infrastructure.account.convertor.AuthorityConvertor;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AuthorityNodeRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AuthorityDo;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AuthorityNodeDo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 权限领域网关实现
 *
 * @author 单开宇
 * @since 2024-02-23
 */
@Component
public class AuthorityGatewayImpl implements AuthorityGateway {


  @Resource
  AuthorityRepository authorityRepository;

  @Resource
  AuthorityNodeRepository authorityNodeRepository;

  @Override
  public void add(Authority authority) {
    AuthorityDo dataObject = AuthorityConvertor.toDataObject(authority);
    authorityRepository.save(dataObject);
    AuthorityNodeDo nodeDataObject = AuthorityConvertor.toNodeDataObject(authority);
    authorityNodeRepository.save(nodeDataObject);
  }
}
