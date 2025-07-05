/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.iam.client.api;

import baby.mumu.iam.client.cmds.PermissionAddCmd;
import baby.mumu.iam.client.cmds.PermissionAddDescendantCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionFindDirectCmd;
import baby.mumu.iam.client.cmds.PermissionFindRootCmd;
import baby.mumu.iam.client.cmds.PermissionUpdateCmd;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllDTO;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionFindAllDTO;
import baby.mumu.iam.client.dto.PermissionFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionFindByCodeDTO;
import baby.mumu.iam.client.dto.PermissionFindByIdDTO;
import baby.mumu.iam.client.dto.PermissionFindDirectDTO;
import baby.mumu.iam.client.dto.PermissionFindRootDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 权限功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
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
  Page<PermissionFindAllDTO> findAll(PermissionFindAllCmd permissionFindAllCmd);

  /**
   * 分页查询权限（不查询总数）
   *
   * @param permissionFindAllSliceCmd 分页查询权限指令
   * @return 查询结果
   */
  Slice<PermissionFindAllSliceDTO> findAllSlice(
    PermissionFindAllSliceCmd permissionFindAllSliceCmd);

  /**
   * 分页查询已归档权限
   *
   * @param permissionArchivedFindAllCmd 分页查询已归档权限指令
   * @return 查询结果
   */
  Page<PermissionArchivedFindAllDTO> findArchivedAll(
    PermissionArchivedFindAllCmd permissionArchivedFindAllCmd);

  /**
   * 分页查询已归档权限（不查询总数）
   *
   * @param permissionArchivedFindAllSliceCmd 分页查询权限指令
   * @return 查询结果
   */
  Slice<PermissionArchivedFindAllSliceDTO> findArchivedAllSlice(
    PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd);

  /**
   * 根据id查询权限
   *
   * @param id 权限ID
   * @return 查询结果
   */
  PermissionFindByIdDTO findById(Long id);

  /**
   * 根据code查询权限
   *
   * @param code 权限编码
   * @return 查询结果
   */
  PermissionFindByCodeDTO findByCode(String code);

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
   * 添加后代权限
   *
   * @param permissionAddDescendantCmd 添加后代权限指令
   */
  void addDescendant(PermissionAddDescendantCmd permissionAddDescendantCmd);

  /**
   * 获取所有根权限
   *
   * @return 根权限
   */
  Page<PermissionFindRootDTO> findRootPermissions(PermissionFindRootCmd permissionFindRootCmd);


  /**
   * 获取直系后代权限
   *
   * @return 直系后代权限
   */
  Page<PermissionFindDirectDTO> findDirectPermissions(
    PermissionFindDirectCmd permissionFindDirectCmd);

  /**
   * 删除权限路径
   *
   * @param ancestorId   祖先ID
   * @param descendantId 后代ID
   */
  void deletePath(Long ancestorId, Long descendantId);

  /**
   * 下载所有权限数据
   *
   * @param response 响应
   */
  void downloadAll(HttpServletResponse response);

  /**
   * 下载所有权限数据（包含权限路径）
   *
   * @param response 响应
   */
  void downloadAllIncludePath(HttpServletResponse response);
}
