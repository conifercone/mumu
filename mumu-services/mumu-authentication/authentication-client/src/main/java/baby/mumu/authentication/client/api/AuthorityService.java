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
package baby.mumu.authentication.client.api;

import baby.mumu.authentication.client.dto.AuthorityAddAncestorCmd;
import baby.mumu.authentication.client.dto.AuthorityAddCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityFindDirectCmd;
import baby.mumu.authentication.client.dto.AuthorityFindRootCmd;
import baby.mumu.authentication.client.dto.AuthorityUpdateCmd;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindByIdCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindDirectCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindRootCo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 权限功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface AuthorityService {

  /**
   * 新增权限
   *
   * @param authorityAddCmd 权限新增指令
   */
  void add(AuthorityAddCmd authorityAddCmd);

  /**
   * 根据id删除权限
   *
   * @param id 权限ID
   */
  void deleteById(Long id);

  /**
   * 根据id更新权限
   *
   * @param authorityUpdateCmd 权限更新指令
   */
  void updateById(AuthorityUpdateCmd authorityUpdateCmd);

  /**
   * 分页查询权限
   *
   * @param authorityFindAllCmd 分页查询权限指令
   * @return 查询结果
   */
  Page<AuthorityFindAllCo> findAll(AuthorityFindAllCmd authorityFindAllCmd);

  /**
   * 分页查询权限（不查询总数）
   *
   * @param authorityFindAllSliceCmd 分页查询权限指令
   * @return 查询结果
   */
  Slice<AuthorityFindAllSliceCo> findAllSlice(AuthorityFindAllSliceCmd authorityFindAllSliceCmd);

  /**
   * 分页查询已归档权限
   *
   * @param authorityArchivedFindAllCmd 分页查询已归档权限指令
   * @return 查询结果
   */
  Page<AuthorityArchivedFindAllCo> findArchivedAll(
    AuthorityArchivedFindAllCmd authorityArchivedFindAllCmd);

  /**
   * 分页查询已归档权限（不查询总数）
   *
   * @param authorityArchivedFindAllSliceCmd 分页查询权限指令
   * @return 查询结果
   */
  Slice<AuthorityArchivedFindAllSliceCo> findArchivedAllSlice(
    AuthorityArchivedFindAllSliceCmd authorityArchivedFindAllSliceCmd);

  /**
   * 根据id查询权限
   *
   * @param id 权限ID
   * @return 查询结果
   */
  AuthorityFindByIdCo findById(Long id);

  /**
   * 根据id归档权限
   *
   * @param id 权限ID
   */
  void archiveById(Long id);

  /**
   * 通过id从归档中恢复
   *
   * @param id 权限ID
   */
  void recoverFromArchiveById(
    Long id);

  /**
   * 添加祖先权限
   *
   * @param authorityAddAncestorCmd 添加祖先权限指令
   */
  void addAncestor(AuthorityAddAncestorCmd authorityAddAncestorCmd);

  /**
   * 获取所有根权限
   *
   * @return 根权限
   */
  Page<AuthorityFindRootCo> findRootAuthorities(AuthorityFindRootCmd authorityFindRootCmd);


  /**
   * 获取直系后代权限
   *
   * @return 直系后代权限
   */
  Page<AuthorityFindDirectCo> findDirectAuthorities(AuthorityFindDirectCmd authorityFindDirectCmd);

  /**
   * 删除权限路径
   *
   * @param ancestorId   祖先ID
   * @param descendantId 后代ID
   */
  void deletePath(Long ancestorId, Long descendantId);
}
