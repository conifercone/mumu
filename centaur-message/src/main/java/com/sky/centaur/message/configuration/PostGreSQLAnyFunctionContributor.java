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

package com.sky.centaur.message.configuration;

import static com.sky.centaur.basis.constants.PgSqlFunctionNameConstants.ANY_PG;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 注册PostGreSQL any函数
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
public class PostGreSQLAnyFunctionContributor implements FunctionContributor {

  @Override
  public void contributeFunctions(@NotNull FunctionContributions functionContributions) {
    functionContributions.getFunctionRegistry().register(
        ANY_PG, new StandardSQLFunction("any", StandardBasicTypes.LONG));
  }
}
