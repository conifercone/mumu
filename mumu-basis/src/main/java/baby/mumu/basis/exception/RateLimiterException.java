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
package baby.mumu.basis.exception;

import baby.mumu.basis.response.ResultCode;
import java.io.Serial;
import lombok.Getter;

/**
 * 限流异常
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
@Getter
public class RateLimiterException extends MuMuException {

  @Serial
  private static final long serialVersionUID = 6238973755219029059L;

  public RateLimiterException(long remainingWaitingTime) {
    super(ResultCode.TOO_MANY_REQUESTS, remainingWaitingTime);
  }
}
