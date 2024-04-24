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

import com.sky.centaur.authentication.domain.role.Role;
import com.sky.centaur.authentication.domain.role.gateway.RoleGateway;
import com.sky.centaur.authentication.infrastructure.role.convertor.RoleConvertor;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleNodeRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleNodeDo;
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
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
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

  @Resource
  RoleRepository roleRepository;

  @Resource
  RoleNodeRepository roleNodeRepository;

  @Resource
  DistributedLock distributedLock;

  @Override
  @Transactional
  @API(status = Status.STABLE)
  public void add(Role role) {
    RoleDo roleDo = RoleConvertor.toDataObject(role);
    roleRepository.persist(roleDo);
    roleNodeRepository.save(RoleConvertor.toNodeDataObject(role));
  }

  @Override
  @Transactional
  @API(status = Status.STABLE)
  public void delete(@NotNull Role role) {
    roleRepository.deleteById(role.getId());
    roleNodeRepository.deleteById(role.getId());
  }

  @Override
  @Transactional
  @API(status = Status.STABLE)
  public void updateById(@NotNull Role role) {
    Optional<RoleDo> roleDoOptional = roleRepository.findById(role.getId());
    Optional<RoleNodeDo> roleNodeDoOptional = roleNodeRepository.findById(role.getId());
    if (roleDoOptional.isPresent() && roleNodeDoOptional.isPresent()) {
      distributedLock.lock();
      RoleDo roleDo = RoleConvertor.toDataObject(role);
      RoleDo target = roleDoOptional.get();
      BeanUtil.jpaUpdate(roleDo, target);
      roleRepository.merge(target);
      RoleNodeDo nodeDataObject = RoleConvertor.toNodeDataObject(role);
      RoleNodeDo roleNodeDo = roleNodeDoOptional.get();
      BeanUtils.copyProperties(nodeDataObject, roleNodeDo,
          BeanUtil.getNullPropertyNames(nodeDataObject));
      roleNodeRepository.deleteById(roleNodeDo.getId());
      roleNodeRepository.save(roleNodeDo);
      distributedLock.unlock();
    } else {
      throw new CentaurException(ResultCode.DATA_DOES_NOT_EXIST);
    }
  }

  @Override
  @Transactional
  @API(status = Status.STABLE)
  public Page<Role> findAll(Role role, int pageNo, int pageSize) {
    Specification<RoleDo> roleDoSpecification = (root, query, cb) -> {
      //noinspection DuplicatedCode
      List<Predicate> predicateList = new ArrayList<>();
      if (StringUtils.hasText(role.getCode())) {
        predicateList.add(cb.like(root.get("code"), "%" + role.getCode() + "%"));
      }
      if (StringUtils.hasText(role.getName())) {
        predicateList.add(cb.like(root.get("name"), "%" + role.getName() + "%"));
      }
      if (role.getId() != null) {
        predicateList.add(cb.equal(root.get("id"), role.getId()));
      }
      if (!CollectionUtils.isEmpty(role.getAuthorities())) {
        role.getAuthorities().forEach(authority -> predicateList.add(cb.equal(
            cb.literal(authority.getId()),
            cb.function("any_pg", Long.class, root.get("authorities"))
        )));
      }
      return query.orderBy(cb.desc(root.get("creationTime")))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
    };
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Page<RoleDo> repositoryAll = roleRepository.findAll(roleDoSpecification,
        pageRequest);

    List<Role> roles = repositoryAll.getContent().stream()
        .map(RoleConvertor::toEntity)
        .toList();
    return new PageImpl<>(roles, pageRequest, repositoryAll.getTotalElements());
  }
}
