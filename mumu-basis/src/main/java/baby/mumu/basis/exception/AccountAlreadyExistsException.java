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

package baby.mumu.basis.exception;

import baby.mumu.basis.response.ResponseCode;
import java.io.Serial;

/**
 * 账号已存在
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class AccountAlreadyExistsException extends ApplicationException {

  @Serial
  private static final long serialVersionUID = 7383143329621946676L;

  public AccountAlreadyExistsException(String accountName) {
    super(ResponseCode.ACCOUNT_ALREADY_EXISTS, accountName);
  }
}
