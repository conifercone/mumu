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
package com.sky.centaur.authentication.filter.datasource.p6spy;

import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.logging.LoggingEventListener;
import java.sql.SQLException;

/**
 * LoggingEventListener
 *
 * @author 单开宇
 * @since 2024-01-22
 */
public class AuthenticationLoggingEventListener extends LoggingEventListener {

  private static AuthenticationLoggingEventListener INSTANCE;

  public static AuthenticationLoggingEventListener getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new AuthenticationLoggingEventListener();
    }
    return INSTANCE;
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos,
      int[] updateCounts, SQLException e) {
    //ignore batch execution results
  }

}
