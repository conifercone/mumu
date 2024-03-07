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
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    Optional<AuthorityDo> authorityDoOptional = authorityRepository.findById(authority.getId());
    Optional<AuthorityNodeDo> nodeDoOptional = authorityNodeRepository.findById(authority.getId());
    if (authorityDoOptional.isPresent() && nodeDoOptional.isPresent()) {
      distributedLock.lock();
      AuthorityDo dataObject = AuthorityConvertor.toDataObject(authority);
      AuthorityDo target = authorityDoOptional.get();
      BeanUtil.jpaUpdate(dataObject, target);
      authorityRepository.save(target);
      AuthorityNodeDo nodeDataObject = AuthorityConvertor.toNodeDataObject(authority);
      AuthorityNodeDo targetNode = nodeDoOptional.get();
      BeanUtils.copyProperties(nodeDataObject, targetNode,
          BeanUtil.getNullPropertyNames(nodeDataObject));
      authorityNodeRepository.save(targetNode);
      distributedLock.unlock();
    } else {
      throw new CentaurException(ResultCode.DATA_DOES_NOT_EXIST);
    }
  }

  @Override
  public Page<Authority> findAll(Authority authority, int pageNo, int pageSize) {
    Specification<AuthorityDo> authorityDoSpecification = (root, query, cb) -> {
      List<Predicate> predicateList = new ArrayList<>();
      if (StringUtils.hasText(authority.getCode())) {
        predicateList.add(cb.like(root.get("code"), "%" + authority.getCode() + "%"));
      }
      if (StringUtils.hasText(authority.getName())) {
        predicateList.add(cb.like(root.get("name"), "%" + authority.getName() + "%"));
      }
      if (authority.getId() != null) {
        predicateList.add(cb.equal(root.get("id"), authority.getId()));
      }
      return query.orderBy(cb.desc(root.get("creationTime")))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
    };
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Page<AuthorityDo> repositoryAll = authorityRepository.findAll(authorityDoSpecification,
        pageRequest);

    List<Authority> authorities = repositoryAll.getContent().stream()
        .map(AuthorityConvertor::toEntity)
        .toList();
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }
}
