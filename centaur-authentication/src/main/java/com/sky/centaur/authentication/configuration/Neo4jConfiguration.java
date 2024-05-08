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
package com.sky.centaur.authentication.configuration;

import com.sky.centaur.basis.constants.BeanNameConstant;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.transaction.TransactionManager;

/**
 * neo4j配置
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
public class Neo4jConfiguration {

  @Bean(BeanNameConstant.NEO4J_TRANSACTION_MANAGER_BEAN_NAME)
  Neo4jTransactionManager neo4jTransactionManager(
      Driver driver, DatabaseSelectionProvider databaseNameProvider,
      ObjectProvider<TransactionManagerCustomizers> optionalCustomizers) {
    Neo4jTransactionManager transactionManager = new Neo4jTransactionManager(driver,
        databaseNameProvider);
    optionalCustomizers.ifAvailable(
        (customizer) -> customizer.customize((TransactionManager) transactionManager));
    return transactionManager;
  }
}
