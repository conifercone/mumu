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

package com.sky.centaur.authentication.infrastructure.authority.gatewayimpl;

import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.domain.authority.gateway.AuthorityGateway;
import com.sky.centaur.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityNodeRepository;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityNodeDo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.tools.BeanUtil;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 权限领域网关实现
 *
 * @author 单开宇
 * @since 2024-02-23
 */
@Component
@Observed(name = "AuthorityGatewayImpl")
public class AuthorityGatewayImpl implements AuthorityGateway {


  @Resource
  AuthorityRepository authorityRepository;

  @Resource
  AuthorityNodeRepository authorityNodeRepository;

  @Resource
  DistributedLock distributedLock;

  @Override
  @Transactional
  public void add(Authority authority) {
    AuthorityDo dataObject = AuthorityConvertor.toDataObject(authority);
    if (authorityRepository.findById(authority.getId()).isPresent()) {
      throw new CentaurException(ResultCode.DATA_ALREADY_EXISTS, authority.getId());
    }
    authorityRepository.save(dataObject);
    AuthorityNodeDo nodeDataObject = AuthorityConvertor.toNodeDataObject(authority);
    authorityNodeRepository.save(nodeDataObject);
  }

  @Override
  @Transactional
  public void delete(@NotNull Authority authority) {
    authorityRepository.deleteById(authority.getId());
    authorityNodeRepository.deleteById(authority.getId());
  }

  @Override
  @Transactional
  public void updateById(@NotNull Authority authority) {
    if (authorityRepository.findById(authority.getId()).isPresent()
        && authorityNodeRepository.findById(authority.getId()).isPresent()) {
      distributedLock.lock();
      AuthorityDo dataObject = AuthorityConvertor.toDataObject(authority);
      AuthorityDo target = authorityRepository.findById(authority.getId()).get();
      BeanUtil.jpaUpdate(dataObject, target);
      authorityRepository.save(target);
      AuthorityNodeDo nodeDataObject = AuthorityConvertor.toNodeDataObject(authority);
      AuthorityNodeDo targetNode = authorityNodeRepository.findById(authority.getId()).get();
      BeanUtils.copyProperties(nodeDataObject, targetNode,
          BeanUtil.getNullPropertyNames(nodeDataObject));
      authorityNodeRepository.save(targetNode);
      distributedLock.unlock();
    } else {
      throw new CentaurException(ResultCode.DATA_DOES_NOT_EXIST);
    }
  }
}
