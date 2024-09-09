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

package baby.mumu.authentication.infrastructure.authority.gatewayimpl;

import static baby.mumu.basis.constants.CommonConstants.LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE;

import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.authority.gateway.AuthorityGateway;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityArchivedRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityArchivedDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityArchivedDo_;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo_;
import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.extension.distributed.lock.DistributedLock;
import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 权限领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AuthorityGatewayImpl")
public class AuthorityGatewayImpl implements AuthorityGateway {


  private final AuthorityRepository authorityRepository;
  private final DistributedLock distributedLock;
  private final RoleGateway roleGateway;
  private final AuthorityConvertor authorityConvertor;
  private final AuthorityArchivedRepository authorityArchivedRepository;
  private final JobScheduler jobScheduler;
  private final ExtensionProperties extensionProperties;

  @Autowired
  public AuthorityGatewayImpl(AuthorityRepository authorityRepository,
      ObjectProvider<DistributedLock> distributedLockObjectProvider, RoleGateway roleGateway,
      AuthorityConvertor authorityConvertor,
      AuthorityArchivedRepository authorityArchivedRepository, JobScheduler jobScheduler,
      ExtensionProperties extensionProperties) {
    this.authorityRepository = authorityRepository;
    this.roleGateway = roleGateway;
    this.authorityConvertor = authorityConvertor;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
    this.authorityArchivedRepository = authorityArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(Authority authority) {
    Optional.ofNullable(authority).flatMap(authorityConvertor::toDataObject)
        .filter(authorityDo -> !authorityRepository.existsByIdOrCode(authorityDo.getId(),
            authorityDo.getCode()) && !authorityArchivedRepository.existsByIdOrCode(
            authorityDo.getId(),
            authorityDo.getCode()))
        .ifPresentOrElse(authorityRepository::persist, () -> {
          throw new MuMuException(ResultCode.AUTHORITY_CODE_OR_ID_ALREADY_EXISTS);
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("删除权限")
  public void deleteById(Long id) {
    Page<Role> authorities = roleGateway.findAllContainAuthority(id, 0, 10);
    if (!authorities.isEmpty()) {
      throw new MuMuException(ResultCode.AUTHORITY_IS_IN_USE_AND_CANNOT_BE_REMOVED,
          authorities.getContent().stream().map(Role::getCode).toList());
    }
    Optional.ofNullable(id).ifPresent(authorityId -> {
      authorityRepository.deleteById(authorityId);
      authorityArchivedRepository.deleteById(authorityId);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(Authority authority) {
    Optional.ofNullable(authority).flatMap(authorityConvertor::toDataObject)
        .ifPresent(dataObject -> {
          Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
          try {
            authorityRepository.merge(dataObject);
          } finally {
            Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
          }
        });
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<Authority> findAll(Authority authority, int pageNo, int pageSize) {
    Specification<AuthorityDo> authorityDoSpecification = (root, query, cb) -> {
      List<Predicate> predicateList = new ArrayList<>();
      Optional.ofNullable(authority.getCode()).filter(StringUtils::isNotBlank)
          .ifPresent(code -> predicateList.add(cb.like(root.get(AuthorityDo_.code),
              String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, code))));
      Optional.ofNullable(authority.getName()).filter(StringUtils::isNotBlank)
          .ifPresent(name -> predicateList.add(cb.like(root.get(AuthorityDo_.name),
              String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, name))));
      Optional.ofNullable(authority.getId())
          .ifPresent(id -> predicateList.add(cb.equal(root.get(AuthorityDo_.id), id)));
      assert query != null;
      return query.orderBy(cb.desc(root.get(AuthorityDo_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
    };
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Page<AuthorityDo> repositoryAll = authorityRepository.findAll(authorityDoSpecification,
        pageRequest);
    List<Authority> authorities = repositoryAll.getContent().stream()
        .map(authorityConvertor::toEntity)
        .filter(Optional::isPresent).map(Optional::get)
        .toList();
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "2.0.0")
  public Page<Authority> findArchivedAll(Authority authority, int pageNo, int pageSize) {
    Specification<AuthorityArchivedDo> authorityArchivedDoSpecification = (root, query, cb) -> {
      List<Predicate> predicateList = new ArrayList<>();
      Optional.ofNullable(authority.getCode()).filter(StringUtils::isNotBlank)
          .ifPresent(code -> predicateList.add(cb.like(root.get(AuthorityArchivedDo_.code),
              String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, code))));
      Optional.ofNullable(authority.getName()).filter(StringUtils::isNotBlank)
          .ifPresent(name -> predicateList.add(cb.like(root.get(AuthorityArchivedDo_.name),
              String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, name))));
      Optional.ofNullable(authority.getId())
          .ifPresent(id -> predicateList.add(cb.equal(root.get(AuthorityArchivedDo_.id), id)));
      assert query != null;
      return query.orderBy(cb.desc(root.get(AuthorityArchivedDo_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
    };
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Page<AuthorityArchivedDo> repositoryAll = authorityArchivedRepository.findAll(
        authorityArchivedDoSpecification,
        pageRequest);
    List<Authority> authorities = repositoryAll.getContent().stream()
        .map(authorityConvertor::toEntity)
        .filter(Optional::isPresent).map(Optional::get)
        .toList();
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Optional<Authority> findById(Long id) {
    return Optional.ofNullable(id).flatMap(authorityRepository::findById).flatMap(
        authorityConvertor::toEntity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档权限")
  public void archiveById(Long id) {
    Page<Role> authorities = roleGateway.findAllContainAuthority(id, 0, 10);
    if (!authorities.isEmpty()) {
      throw new MuMuException(ResultCode.AUTHORITY_IS_IN_USE_AND_CANNOT_BE_ARCHIVE,
          authorities.getContent().stream().map(Role::getCode).toList());
    }
    Optional.ofNullable(id).flatMap(authorityRepository::findById)
        .flatMap(authorityConvertor::toArchivedDo).ifPresent(authorityArchivedDo -> {
          authorityArchivedDo.setArchived(true);
          authorityArchivedRepository.persist(authorityArchivedDo);
          authorityRepository.deleteById(authorityArchivedDo.getId());
          GlobalProperties global = extensionProperties.getGlobal();
          jobScheduler.schedule(Instant.now()
                  .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
              () -> deleteArchivedDataJob(authorityArchivedDo.getId()));
        });
  }

  @Job
  @DangerousOperation("根据ID删除权限归档数据定时任务")
  private void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id)
        .filter(authorityId -> roleGateway.findAllContainAuthority(authorityId, 0, 10).isEmpty())
        .ifPresent(authorityArchivedRepository::deleteById);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(authorityArchivedRepository::findById)
        .flatMap(authorityConvertor::toDataObject).ifPresent(authorityDo -> {
          authorityDo.setArchived(false);
          authorityArchivedRepository.deleteById(authorityDo.getId());
          authorityRepository.persist(authorityDo);
        });
  }
}
