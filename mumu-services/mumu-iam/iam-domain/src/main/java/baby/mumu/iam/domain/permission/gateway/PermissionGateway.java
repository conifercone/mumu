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

package baby.mumu.iam.domain.permission.gateway;

import baby.mumu.iam.domain.permission.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 权限领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public interface PermissionGateway {

    /**
     * 添加权限
     *
     * @param permission 权限信息
     * @return 权限ID
     */
    Long add(Permission permission);

    /**
     * 根据id删除权限
     *
     * @param id 权限id
     */
    void deleteById(Long id);

    /**
     * 根据id更新权限
     *
     * @param permission 目标权限信息
     * @return 修改后数据
     */
    Optional<Permission> updateById(Permission permission);

    /**
     * 分页查询权限
     *
     * @param permission 查询条件
     * @param current    页码
     * @param pageSize   每页数量
     * @return 查询结果
     */
    Page<Permission> findAll(Permission permission, int current, int pageSize);

    /**
     * 切片分页查询权限（不查询总数）
     *
     * @param permission 查询条件
     * @param current    页码
     * @param pageSize   当前页数量
     * @return 查询结果
     */
    Slice<Permission> findAllSlice(Permission permission, int current, int pageSize);

    /**
     * 切片分页查询已归档的权限（不查询总数）
     *
     * @param permission 查询条件
     * @param current    页码
     * @param pageSize   当前页数量
     * @return 查询结果
     */
    Slice<Permission> findArchivedAllSlice(Permission permission, int current, int pageSize);

    /**
     * 分页查询已归档的权限
     *
     * @param permission 查询条件
     * @param current    页码
     * @param pageSize   每页数量
     * @return 查询结果
     */
    Page<Permission> findArchivedAll(Permission permission, int current, int pageSize);

    /**
     * 根据id查询权限详情
     *
     * @param id 权限id
     * @return 权限信息
     */
    Optional<Permission> findById(Long id);


    /**
     * 根据id归档权限
     *
     * @param id 权限id
     */
    void archiveById(Long id);

    /**
     * 通过id从归档中恢复
     *
     * @param id 权限id
     */
    void recoverFromArchiveById(Long id);

    /**
     * 给指定祖先权限添加后代权限
     *
     * @param ancestorId   祖先权限ID
     * @param descendantId 后代权限ID
     */
    void addDescendant(Long ancestorId, Long descendantId);

    /**
     * 获取所有根权限
     *
     * @return 根权限列表
     */
    Page<Permission> findRootPermissions(int current, int pageSize);

    /**
     * 获取直系后代权限
     *
     * @param ancestorId 祖先ID
     * @param current    当前页
     * @param pageSize   每页数量
     * @return 直系后代
     */
    Page<Permission> findDirectPermissions(Long ancestorId, int current, int pageSize);


    /**
     * 删除权限路径
     *
     * @param ancestorId   祖先权限ID
     * @param descendantId 后代权限ID
     */
    void deletePath(Long ancestorId, Long descendantId);

    /**
     * 根据编码删除权限
     *
     * @param code 权限编码
     */
    void deleteByCode(String code);

    /**
     * 获取所有权限
     *
     * @return 权限流
     */
    Stream<Permission> findAll();


    /**
     * 获取所有权限
     *
     * @return 权限流
     */
    Stream<Permission> findAllIncludePath();

    /**
     * 根据code查询权限
     *
     * @param code 权限编码
     * @return 权限
     */
    Optional<Permission> findByCode(String code);
}
