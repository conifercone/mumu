/*
 * Copyright (c) 2024-2024, the original author or authors.
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

import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.authority.gateway.AuthorityGateway;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityArchivedRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityArchivedDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.redis.AuthorityRedisRepository;
import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.extension.distributed.lock.DistributedLock;
import io.micrometer.observation.annotation.Observed;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
  private final AuthorityRedisRepository authorityRedisRepository;

  @Autowired
  public AuthorityGatewayImpl(AuthorityRepository authorityRepository,
      ObjectProvider<DistributedLock> distributedLockObjectProvider, RoleGateway roleGateway,
      AuthorityConvertor authorityConvertor,
      AuthorityArchivedRepository authorityArchivedRepository, JobScheduler jobScheduler,
      ExtensionProperties extensionProperties, AuthorityRedisRepository authorityRedisRepository) {
    this.authorityRepository = authorityRepository;
    this.roleGateway = roleGateway;
    this.authorityConvertor = authorityConvertor;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
    this.authorityArchivedRepository = authorityArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
    this.authorityRedisRepository = authorityRedisRepository;
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
        .ifPresentOrElse(authorityDo -> {
          authorityRepository.persist(authorityDo);
          authorityRedisRepository.deleteById(authorityDo.getId());
        }, () -> {
          throw new MuMuException(ResponseCode.AUTHORITY_CODE_OR_ID_ALREADY_EXISTS);
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("根据ID删除ID为%0的权限数据")
  public void deleteById(Long id) {
    List<Role> authorities = roleGateway.findAllContainAuthority(id);
    if (CollectionUtils.isNotEmpty(authorities)) {
      throw new MuMuException(ResponseCode.AUTHORITY_IS_IN_USE_AND_CANNOT_BE_REMOVED,
          authorities.stream().map(Role::getCode).toList());
    }
    Optional.ofNullable(id).ifPresent(authorityId -> {
      authorityRepository.deleteById(authorityId);
      authorityArchivedRepository.deleteById(authorityId);
      authorityRedisRepository.deleteById(authorityId);
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
            authorityRedisRepository.deleteById(dataObject.getId());
          } finally {
            Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
          }
        });
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<Authority> findAll(Authority authority, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<AuthorityDo> repositoryAll = authorityRepository.findAllPage(
        authorityConvertor.toDataObject(authority).orElseGet(AuthorityDo::new),
        pageRequest);
    List<Authority> authorities = repositoryAll.getContent().stream()
        .map(authorityConvertor::toEntity)
        .filter(Optional::isPresent).map(Optional::get)
        .toList();
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<Authority> findAllSlice(Authority authority, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<AuthorityDo> authorityDoSlice = authorityRepository.findAllSlice(
        authorityConvertor.toDataObject(authority).orElseGet(AuthorityDo::new), pageRequest);
    return new SliceImpl<>(authorityDoSlice.getContent().stream()
        .flatMap(authorityDo -> authorityConvertor.toEntity(authorityDo).stream())
        .toList(), pageRequest, authorityDoSlice.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<Authority> findArchivedAllSlice(Authority authority, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<AuthorityArchivedDo> authorityArchivedDos = authorityArchivedRepository.findAllSlice(
        authorityConvertor.toArchivedDo(authority).orElseGet(AuthorityArchivedDo::new),
        pageRequest);
    return new SliceImpl<>(authorityArchivedDos.getContent().stream()
        .flatMap(authorityArchivedDo -> authorityConvertor.toEntity(authorityArchivedDo).stream())
        .toList(), pageRequest, authorityArchivedDos.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.0.0")
  public Page<Authority> findArchivedAll(Authority authority, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<AuthorityArchivedDo> repositoryAll = authorityArchivedRepository.findAllPage(
        authorityConvertor.toArchivedDo(authority).orElseGet(AuthorityArchivedDo::new),
        pageRequest);
    List<Authority> authorities = repositoryAll.getContent().stream()
        .map(authorityConvertor::toEntity)
        .filter(Optional::isPresent).map(Optional::get)
        .toList();
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Optional<Authority> findById(Long id) {
    return Optional.ofNullable(id).flatMap(authorityRedisRepository::findById).flatMap(
        authorityConvertor::toEntity).or(() -> {
      Optional<Authority> authority = authorityRepository.findById(id)
          .flatMap(authorityConvertor::toEntity);
      authority.flatMap(authorityConvertor::toAuthorityRedisDo)
          .ifPresent(authorityRedisRepository::save);
      return authority;
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档ID为%0的权限")
  public void archiveById(Long id) {
    List<Role> authorities = roleGateway.findAllContainAuthority(id);
    if (CollectionUtils.isNotEmpty(authorities)) {
      throw new MuMuException(ResponseCode.AUTHORITY_IS_IN_USE_AND_CANNOT_BE_ARCHIVE,
          authorities.stream().map(Role::getCode).toList());
    }
    //noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(authorityRepository::findById)
        .flatMap(authorityConvertor::toArchivedDo).ifPresent(authorityArchivedDo -> {
          authorityArchivedDo.setArchived(true);
          authorityArchivedRepository.persist(authorityArchivedDo);
          authorityRepository.deleteById(authorityArchivedDo.getId());
          authorityRedisRepository.deleteById(authorityArchivedDo.getId());
          GlobalProperties global = extensionProperties.getGlobal();
          jobScheduler.schedule(Instant.now()
                  .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
              () -> deleteArchivedDataJob(authorityArchivedDo.getId()));
        });
  }

  @Job(name = "删除ID为：%0 的权限归档数据")
  @DangerousOperation("根据ID删除ID为%0的权限归档数据定时任务")
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id)
        .filter(authorityId -> roleGateway.findAllContainAuthority(authorityId).isEmpty())
        .ifPresent(authorityId -> {
          authorityArchivedRepository.deleteById(authorityId);
          authorityRedisRepository.deleteById(authorityId);
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(authorityArchivedRepository::findById)
        .flatMap(authorityConvertor::toDataObject).ifPresent(authorityDo -> {
          authorityDo.setArchived(false);
          authorityArchivedRepository.deleteById(authorityDo.getId());
          authorityRepository.persist(authorityDo);
          authorityRedisRepository.deleteById(authorityDo.getId());
        });
  }
}
