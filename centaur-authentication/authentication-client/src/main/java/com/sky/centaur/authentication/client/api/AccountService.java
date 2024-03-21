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

import com.sky.centaur.authentication.client.dto.AccountDisableCmd;
import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.client.dto.AccountUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import com.sky.centaur.authentication.client.dto.co.AccountDisableCo;
import com.sky.centaur.authentication.client.dto.co.AccountOnlineStatisticsCo;
import com.sky.centaur.authentication.client.dto.co.AccountRegisterCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateCo;

/**
 * 账户功能API
 *
 * @author 单开宇
 * @since 2024-01-15
 */
public interface AccountService {

  AccountRegisterCo register(AccountRegisterCmd accountRegisterCmd);

  AccountUpdateCo updateById(AccountUpdateCmd accountUpdateCmd);

  AccountDisableCo disable(AccountDisableCmd accountDisableCmd);

  AccountCurrentLoginQueryCo queryCurrentLoginAccount();

  AccountOnlineStatisticsCo onlineAccounts();
}
