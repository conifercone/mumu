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

import static com.sky.centaur.basis.constants.CommonConstants.LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE;

import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.domain.authority.gateway.AuthorityGateway;
import com.sky.centaur.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo_;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "AuthorityGatewayImpl")
public class AuthorityGatewayImpl implements AuthorityGateway {


  private final AuthorityRepository authorityRepository;

  private final DistributedLock distributedLock;

  @Autowired
  public AuthorityGatewayImpl(AuthorityRepository authorityRepository,
      ObjectProvider<DistributedLock> distributedLockObjectProvider) {
    this.authorityRepository = authorityRepository;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(Authority authority) {
    AuthorityDo dataObject = AuthorityConvertor.toDataObject(authority);
    authorityRepository.findById(authority.getId()).ifPresentOrElse(authorityDo -> {
      throw new CentaurException(ResultCode.DATA_ALREADY_EXISTS, authorityDo.getId());
    }, () -> authorityRepository.persist(dataObject));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void delete(@NotNull Authority authority) {
    authorityRepository.deleteById(authority.getId());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@NotNull Authority authority) {
    distributedLock.lock();
    try {
      AuthorityDo dataObject = AuthorityConvertor.toDataObject(authority);
      authorityRepository.merge(dataObject);
    } finally {
      distributedLock.unlock();
    }
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<Authority> findAll(Authority authority, int pageNo, int pageSize) {
    Specification<AuthorityDo> authorityDoSpecification = (root, query, cb) -> {
      //noinspection DuplicatedCode
      List<Predicate> predicateList = new ArrayList<>();
      if (StringUtils.hasText(authority.getCode())) {
        predicateList.add(cb.like(root.get(AuthorityDo_.code),
            String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, authority.getCode())));
      }
      if (StringUtils.hasText(authority.getName())) {
        predicateList.add(cb.like(root.get(AuthorityDo_.name),
            String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, authority.getName())));
      }
      if (authority.getId() != null) {
        predicateList.add(cb.equal(root.get(AuthorityDo_.id), authority.getId()));
      }
      return query.orderBy(cb.desc(root.get(AuthorityDo_.creationTime)))
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
