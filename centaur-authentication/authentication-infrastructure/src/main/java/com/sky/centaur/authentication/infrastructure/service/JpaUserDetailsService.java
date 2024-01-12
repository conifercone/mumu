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
package com.sky.centaur.authentication.infrastructure.service;

import com.sky.centaur.authentication.application.dto.Account;
import com.sky.centaur.authentication.domain.account.Users;
import com.sky.centaur.authentication.infrastructure.repository.UsersRepository;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * jpa用户详情实现
 *
 * @author 单开宇
 * @since 2024-01-12
 */
public class JpaUserDetailsService implements UserDetailsService {

  @Resource
  UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Users users = usersRepository.findUsersByUsername(username);
    if (null == users) {
      throw new UsernameNotFoundException(username);
    }
    return new Account(users);
  }
}
