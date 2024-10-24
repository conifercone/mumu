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

import baby.mumu.authentication.client.dto.RoleAddCmd;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.RoleFindAllCmd;
import baby.mumu.authentication.client.dto.RoleFindAllSliceCmd;
import baby.mumu.authentication.client.dto.RoleUpdateCmd;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllSliceCo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 角色功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface RoleService {

  /**
   * 新增角色
   *
   * @param roleAddCmd 角色新增指令
   */
  void add(RoleAddCmd roleAddCmd);

  /**
   * 根据id删除角色
   *
   * @param id 角色ID
   */
  void deleteById(Long id);

  /**
   * 根据id更新角色
   *
   * @param roleUpdateCmd 根据id更新角色指令
   */
  void updateById(RoleUpdateCmd roleUpdateCmd);

  /**
   * 分页查询角色
   *
   * @param roleFindAllCmd 分页查询角色指令
   * @return 查询结果
   */
  Page<RoleFindAllCo> findAll(RoleFindAllCmd roleFindAllCmd);

  /**
   * 分页查询角色（不查询总数）
   *
   * @param roleFindAllSliceCmd 分页查询角色指令
   * @return 查询结果
   */
  Slice<RoleFindAllSliceCo> findAllSlice(RoleFindAllSliceCmd roleFindAllSliceCmd);

  /**
   * 分页查询已归档角色
   *
   * @param roleArchivedFindAllCmd 分页查询已归档角色指令
   * @return 查询结果
   */
  Page<RoleArchivedFindAllCo> findArchivedAll(RoleArchivedFindAllCmd roleArchivedFindAllCmd);

  /**
   * 分页查询已归档角色（不查询总数）
   *
   * @param roleArchivedFindAllSliceCmd 分页查询已归档角色指令
   * @return 查询结果
   */
  Slice<RoleArchivedFindAllSliceCo> findArchivedAllSlice(
      RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd);

  /**
   * 根据id归档角色
   *
   * @param id 角色ID
   */
  void archiveById(Long id);

  /**
   * 通过id从归档中恢复
   *
   * @param id 角色ID
   */
  void recoverFromArchiveById(
      Long id);
}
