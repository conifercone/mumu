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

import baby.mumu.authentication.client.dto.PermissionAddAncestorCmd;
import baby.mumu.authentication.client.dto.PermissionAddCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindDirectCmd;
import baby.mumu.authentication.client.dto.PermissionFindRootCmd;
import baby.mumu.authentication.client.dto.PermissionUpdateCmd;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindByIdCo;
import baby.mumu.authentication.client.dto.co.PermissionFindDirectCo;
import baby.mumu.authentication.client.dto.co.PermissionFindRootCo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 权限功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface PermissionService {

  /**
   * 新增权限
   *
   * @param permissionAddCmd 权限新增指令
   */
  void add(PermissionAddCmd permissionAddCmd);

  /**
   * 根据id删除权限
   *
   * @param id 权限ID
   */
  void deleteById(Long id);

  /**
   * 根据code删除权限
   *
   * @param code 权限编码
   */
  void deleteByCode(String code);

  /**
   * 根据id更新权限
   *
   * @param permissionUpdateCmd 权限更新指令
   */
  void updateById(PermissionUpdateCmd permissionUpdateCmd);

  /**
   * 分页查询权限
   *
   * @param permissionFindAllCmd 分页查询权限指令
   * @return 查询结果
   */
  Page<PermissionFindAllCo> findAll(PermissionFindAllCmd permissionFindAllCmd);

  /**
   * 分页查询权限（不查询总数）
   *
   * @param permissionFindAllSliceCmd 分页查询权限指令
   * @return 查询结果
   */
  Slice<PermissionFindAllSliceCo> findAllSlice(PermissionFindAllSliceCmd permissionFindAllSliceCmd);

  /**
   * 分页查询已归档权限
   *
   * @param permissionArchivedFindAllCmd 分页查询已归档权限指令
   * @return 查询结果
   */
  Page<PermissionArchivedFindAllCo> findArchivedAll(
    PermissionArchivedFindAllCmd permissionArchivedFindAllCmd);

  /**
   * 分页查询已归档权限（不查询总数）
   *
   * @param permissionArchivedFindAllSliceCmd 分页查询权限指令
   * @return 查询结果
   */
  Slice<PermissionArchivedFindAllSliceCo> findArchivedAllSlice(
    PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd);

  /**
   * 根据id查询权限
   *
   * @param id 权限ID
   * @return 查询结果
   */
  PermissionFindByIdCo findById(Long id);

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
   * @param permissionAddAncestorCmd 添加祖先权限指令
   */
  void addAncestor(PermissionAddAncestorCmd permissionAddAncestorCmd);

  /**
   * 获取所有根权限
   *
   * @return 根权限
   */
  Page<PermissionFindRootCo> findRootPermissions(PermissionFindRootCmd permissionFindRootCmd);


  /**
   * 获取直系后代权限
   *
   * @return 直系后代权限
   */
  Page<PermissionFindDirectCo> findDirectPermissions(
    PermissionFindDirectCmd permissionFindDirectCmd);

  /**
   * 删除权限路径
   *
   * @param ancestorId   祖先ID
   * @param descendantId 后代ID
   */
  void deletePath(Long ancestorId, Long descendantId);
}
