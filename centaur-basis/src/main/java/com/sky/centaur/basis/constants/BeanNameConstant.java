package com.sky.centaur.basis.constants;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * bean实例名称常量
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@API(status = Status.STABLE, since = "1.0.0")
public final class BeanNameConstant {

  /**
   * 默认事务管理器bean名称
   */
  public static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";

  /**
   * neo4j事务管理器bean名称
   */
  public static final String NEO4J_TRANSACTION_MANAGER_BEAN_NAME = "neo4jTransactionManager";

}

