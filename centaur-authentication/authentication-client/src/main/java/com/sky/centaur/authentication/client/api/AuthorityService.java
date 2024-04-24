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
package com.sky.centaur.authentication.client.api;

import com.sky.centaur.authentication.client.dto.AuthorityAddCmd;
import com.sky.centaur.authentication.client.dto.AuthorityDeleteCmd;
import com.sky.centaur.authentication.client.dto.AuthorityFindAllCmd;
import com.sky.centaur.authentication.client.dto.AuthorityUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.AuthorityAddCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityDeleteCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
import org.springframework.data.domain.Page;

/**
 * 权限功能API
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public interface AuthorityService {

  AuthorityAddCo add(AuthorityAddCmd authorityAddCmd);

  AuthorityDeleteCo delete(AuthorityDeleteCmd authorityDeleteCmd);

  AuthorityUpdateCo updateById(AuthorityUpdateCmd authorityUpdateCmd);

  Page<AuthorityFindAllCo> findAll(AuthorityFindAllCmd authorityFindAllCmd);

}
