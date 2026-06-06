/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.iam.application.service;

import baby.mumu.iam.application.role.executor.*;
import baby.mumu.iam.client.api.RoleService;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Service
@Observed(name = "RoleServiceImpl")
public class RoleServiceImpl implements RoleService {

    private final RoleAddCmdExe roleAddCmdExe;
    private final RoleDeleteByIdCmdExe roleDeleteByIdCmdExe;
    private final RoleUpdateCmdExe roleUpdateCmdExe;
    private final RoleFindAllCmdExe roleFindAllCmdExe;
    private final RoleArchiveByIdCmdExe roleArchiveByIdCmdExe;
    private final RoleRecoverFromArchiveByIdCmdExe roleRecoverFromArchiveByIdCmdExe;
    private final RoleFindAllSliceCmdExe roleFindAllSliceCmdExe;
    private final RoleArchivedFindAllCmdExe roleArchivedFindAllCmdExe;
    private final RoleArchivedFindAllSliceCmdExe roleArchivedFindAllSliceCmdExe;
    private final RoleFindByIdCmdExe roleFindByIdCmdExe;
    private final RoleAddDescendantCmdExe roleAddDescendantCmdExe;
    private final RoleFindRootCmdExe roleFindRootCmdExe;
    private final RoleFindDirectCmdExe roleFindDirectCmdExe;
    private final RoleDeletePathCmdExe roleDeletePathCmdExe;
    private final RoleDeleteByCodeCmdExe roleDeleteByCodeCmdExe;
    private final RoleFindByCodeCmdExe roleFindByCodeCmdExe;

    @Autowired
    public RoleServiceImpl(RoleAddCmdExe roleAddCmdExe, RoleDeleteByIdCmdExe roleDeleteByIdCmdExe,
                           RoleUpdateCmdExe roleUpdateCmdExe, RoleFindAllCmdExe roleFindAllCmdExe,
                           RoleArchiveByIdCmdExe roleArchiveByIdCmdExe,
                           RoleRecoverFromArchiveByIdCmdExe roleRecoverFromArchiveByIdCmdExe,
                           RoleFindAllSliceCmdExe roleFindAllSliceCmdExe,
                           RoleArchivedFindAllCmdExe roleArchivedFindAllCmdExe,
                           RoleArchivedFindAllSliceCmdExe roleArchivedFindAllSliceCmdExe,
                           RoleFindByIdCmdExe roleFindByIdCmdExe, RoleAddDescendantCmdExe roleAddDescendantCmdExe,
                           RoleFindRootCmdExe roleFindRootCmdExe, RoleFindDirectCmdExe roleFindDirectCmdExe,
                           RoleDeletePathCmdExe roleDeletePathCmdExe, RoleDeleteByCodeCmdExe roleDeleteByCodeCmdExe,
                           RoleFindByCodeCmdExe roleFindByCodeCmdExe) {
        this.roleAddCmdExe = roleAddCmdExe;
        this.roleDeleteByIdCmdExe = roleDeleteByIdCmdExe;
        this.roleUpdateCmdExe = roleUpdateCmdExe;
        this.roleFindAllCmdExe = roleFindAllCmdExe;
        this.roleArchiveByIdCmdExe = roleArchiveByIdCmdExe;
        this.roleRecoverFromArchiveByIdCmdExe = roleRecoverFromArchiveByIdCmdExe;
        this.roleFindAllSliceCmdExe = roleFindAllSliceCmdExe;
        this.roleArchivedFindAllCmdExe = roleArchivedFindAllCmdExe;
        this.roleArchivedFindAllSliceCmdExe = roleArchivedFindAllSliceCmdExe;
        this.roleFindByIdCmdExe = roleFindByIdCmdExe;
        this.roleAddDescendantCmdExe = roleAddDescendantCmdExe;
        this.roleFindRootCmdExe = roleFindRootCmdExe;
        this.roleFindDirectCmdExe = roleFindDirectCmdExe;
        this.roleDeletePathCmdExe = roleDeletePathCmdExe;
        this.roleDeleteByCodeCmdExe = roleDeleteByCodeCmdExe;
        this.roleFindByCodeCmdExe = roleFindByCodeCmdExe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(RoleAddCmd roleAddCmd) {
        return roleAddCmdExe.execute(roleAddCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        roleDeleteByIdCmdExe.execute(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCode(String code) {
        roleDeleteByCodeCmdExe.execute(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleUpdatedDataDTO updateById(RoleUpdateCmd roleUpdateCmd) {
        return roleUpdateCmdExe.execute(roleUpdateCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<RoleFindAllDTO> findAll(RoleFindAllCmd roleFindAllCmd) {
        return roleFindAllCmdExe.execute(roleFindAllCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Slice<RoleFindAllSliceDTO> findAllSlice(RoleFindAllSliceCmd roleFindAllSliceCmd) {
        return roleFindAllSliceCmdExe.execute(roleFindAllSliceCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RoleArchivedFindAllDTO> findArchivedAll(
        RoleArchivedFindAllCmd roleArchivedFindAllCmd) {
        return roleArchivedFindAllCmdExe.execute(roleArchivedFindAllCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Slice<RoleArchivedFindAllSliceDTO> findArchivedAllSlice(
        RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd) {
        return roleArchivedFindAllSliceCmdExe.execute(roleArchivedFindAllSliceCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveById(Long id) {
        roleArchiveByIdCmdExe.execute(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverFromArchiveById(Long id) {
        roleRecoverFromArchiveByIdCmdExe.execute(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDescendant(RoleAddDescendantCmd roleAddDescendantCmd) {
        roleAddDescendantCmdExe.execute(roleAddDescendantCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RoleFindRootDTO> findRootRoles(RoleFindRootCmd roleFindRootCmd) {
        return roleFindRootCmdExe.execute(roleFindRootCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RoleFindDirectDTO> findDirectRoles(RoleFindDirectCmd roleFindDirectCmd) {
        return roleFindDirectCmdExe.execute(roleFindDirectCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePath(Long ancestorId, Long descendantId) {
        roleDeletePathCmdExe.execute(ancestorId, descendantId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleFindByIdDTO findById(Long id) {
        return roleFindByIdCmdExe.execute(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleFindByCodeDTO findByCode(String code) {
        return roleFindByCodeCmdExe.execute(code);
    }
}
