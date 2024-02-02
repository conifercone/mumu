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
package com.sky.centaur.authentication.filter.datasource;

import com.p6spy.engine.spy.P6DataSource;
import com.sky.centaur.authentication.infrastructure.config.AuthenticationProperties;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * p6spy数据源过滤器
 *
 * @author 单开宇
 * @since 2024-01-22
 */
public class P6spyDataSourceFilter extends AbstractDataSourceFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(P6spyDataSourceFilter.class);


  @Override
  public DataSource afterCreate(DataSource dataSource,
      AuthenticationProperties authenticationProperties) {
    LOGGER.debug("P6spyDataSourceFilter afterCreate starting...");
    boolean enableLog =
        authenticationProperties.isEnableLog();
    if (enableLog) {
      checkJar();
      dataSource = new P6DataSource(dataSource);
      LOGGER.debug("p6spy datasource wrapped datasource");
    }

    return dataSource;
  }

  /**
   * 校验是否引入相关jar
   */
  private void checkJar() {
    try {
      Class.forName("com.p6spy.engine.spy.P6DataSource");
    } catch (Exception e) {
      throw new CentaurException(ResultCode.MISSING_P6SPY_DEPENDENCY_INFORMATION);
    }
  }
}
