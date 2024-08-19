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

package com.sky.centaur.authentication.infrastructure.role.gatewayimpl;

import static com.sky.centaur.basis.constants.CommonConstants.LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE;
import static com.sky.centaur.basis.constants.PgSqlFunctionNameConstants.ANY_PG;

import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import com.sky.centaur.authentication.domain.role.Role;
import com.sky.centaur.authentication.domain.role.gateway.RoleGateway;
import com.sky.centaur.authentication.infrastructure.role.convertor.RoleConvertor;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleArchivedRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo_;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo_;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 角色领域网关实现
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "RoleGatewayImpl")
public class RoleGatewayImpl implements RoleGateway {

  private final RoleRepository roleRepository;
  private final DistributedLock distributedLock;
  private final AccountGateway accountGateway;
  private final RoleConvertor roleConvertor;
  private final RoleArchivedRepository roleArchivedRepository;

  public RoleGatewayImpl(RoleRepository roleRepository,
      ObjectProvider<DistributedLock> distributedLockObjectProvider,
      AccountGateway accountGateway, RoleConvertor roleConvertor,
      RoleArchivedRepository roleArchivedRepository) {
    this.roleRepository = roleRepository;
    this.accountGateway = accountGateway;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
    this.roleConvertor = roleConvertor;
    this.roleArchivedRepository = roleArchivedRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(Role role) {
    Optional.ofNullable(role).flatMap(roleConvertor::toDataObject)
        .filter(roleDo -> !roleRepository.existsByIdOrCode(roleDo.getId(), roleDo.getCode())
            && !roleArchivedRepository.existsByIdOrCode(roleDo.getId(), roleDo.getCode()))
        .ifPresentOrElse(roleRepository::persist, () -> {
          throw new CentaurException(ResultCode.ROLE_CODE_OR_ID_ALREADY_EXISTS);
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void deleteById(Long id) {
    Optional.ofNullable(id).ifPresent(roleId -> {
      Page<Account> allAccountByRoleId = accountGateway.findAllAccountByRoleId(roleId, 0, 10);
      if (!CollectionUtils.isEmpty(allAccountByRoleId.getContent())) {
        throw new CentaurException(ResultCode.ROLE_IS_IN_USE_AND_CANNOT_BE_REMOVED,
            allAccountByRoleId.getContent().stream().map(Account::getUsername).toList());
      }
      roleRepository.deleteById(roleId);
      roleArchivedRepository.deleteById(roleId);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(Role role) {
    Optional.ofNullable(role).ifPresent(roleDomain -> {
      Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
      try {
        roleConvertor.toDataObject(roleDomain).ifPresent(roleRepository::merge);
      } finally {
        Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
      }
    });
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Page<Role> findAll(Role role, int pageNo, int pageSize) {
    Specification<RoleDo> roleDoSpecification = (root, query, cb) -> {
      List<Predicate> predicateList = new ArrayList<>();
      Optional.ofNullable(role.getCode()).filter(StringUtils::hasText)
          .ifPresent(code -> predicateList.add(cb.like(root.get(RoleDo_.code),
              String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, code))));
      Optional.ofNullable(role.getName()).filter(StringUtils::hasText)
          .ifPresent(name -> predicateList.add(cb.like(root.get(RoleDo_.name),
              String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, name))));
      Optional.ofNullable(role.getId())
          .ifPresent(id -> predicateList.add(cb.equal(root.get(RoleDo_.id), id)));
      Optional.ofNullable(role.getAuthorities()).filter(authorities -> !authorities.isEmpty())
          .ifPresent(authorities -> authorities.forEach(authority -> predicateList.add(cb.equal(
              cb.literal(authority.getId()),
              cb.function(ANY_PG, Long.class, root.get(RoleDo_.authorities))
          ))));
      assert query != null;
      return query.orderBy(cb.desc(root.get(RoleDo_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
    };
    return getRoles(pageNo, pageSize, roleDoSpecification);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Page<Role> findAllContainAuthority(Long authorityId, int pageNo, int pageSize) {
    Specification<RoleDo> roleDoSpecification = (root, query, cb) -> {
      List<Predicate> predicateList = new ArrayList<>();
      Optional.ofNullable(authorityId)
          .ifPresent(id -> predicateList.add(cb.equal(
              cb.literal(id),
              cb.function(ANY_PG, Long.class, root.get(RoleDo_.authorities))
          )));
      assert query != null;
      return query.orderBy(cb.desc(root.get(RoleDo_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
    };
    Page<Role> rolePage = getRoles(pageNo, pageSize, roleDoSpecification);
    if (CollectionUtils.isEmpty(rolePage.getContent())) {
      Specification<RoleArchivedDo> roleArchivedDoSpecification = (root, query, cb) -> {
        List<Predicate> predicateList = new ArrayList<>();
        Optional.ofNullable(authorityId)
            .ifPresent(id -> predicateList.add(cb.equal(
                cb.literal(id),
                cb.function(ANY_PG, Long.class, root.get(RoleArchivedDo_.authorities))
            )));
        assert query != null;
        return query.orderBy(cb.desc(root.get(RoleArchivedDo_.creationTime)))
            .where(predicateList.toArray(new Predicate[0]))
            .getRestriction();
      };
      PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
      Page<RoleArchivedDo> repositoryAll = roleArchivedRepository.findAll(
          roleArchivedDoSpecification,
          pageRequest);
      List<Role> roles = repositoryAll.getContent().stream()
          .map(roleConvertor::toEntity)
          .filter(Optional::isPresent).map(Optional::get)
          .toList();
      return new PageImpl<>(roles, pageRequest, repositoryAll.getTotalElements());
    }
    return rolePage;
  }

  @NotNull
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  protected Page<Role> getRoles(int pageNo, int pageSize,
      Specification<RoleDo> roleDoSpecification) {
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Page<RoleDo> repositoryAll = roleRepository.findAll(roleDoSpecification,
        pageRequest);
    List<Role> roles = repositoryAll.getContent().stream()
        .map(roleConvertor::toEntity)
        .filter(Optional::isPresent).map(Optional::get)
        .toList();
    return new PageImpl<>(roles, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(Long id) {
    Optional.ofNullable(id).flatMap(roleRepository::findById)
        .flatMap(roleConvertor::toArchivedDo).ifPresent(roleArchivedDo -> {
          roleArchivedDo.setArchived(true);
          roleArchivedRepository.persist(roleArchivedDo);
          roleRepository.deleteById(roleArchivedDo.getId());
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(roleArchivedRepository::findById)
        .flatMap(roleConvertor::toDataObject).ifPresent(roleDo -> {
          roleDo.setArchived(false);
          roleArchivedRepository.deleteById(roleDo.getId());
          roleRepository.persist(roleDo);
        });
  }
}
