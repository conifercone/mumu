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
