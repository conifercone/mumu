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
package baby.mumu.unique.client.config;

import baby.mumu.basis.kotlin.tools.SpringContextUtil;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * 雪花算法ID生成器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

  private Class<?> idType;

  @Override
  public Object generate(@NotNull SharedSessionContractImplementor session, Object object) {
    PrimaryKeyGrpcService primaryKeyGrpcService = SpringContextUtil.getBean(
        PrimaryKeyGrpcService.class)
      .orElseThrow(() -> new IllegalArgumentException("PrimaryKeyGrpcService bean not found"));
    if (String.class.isAssignableFrom(idType)) {
      return String.valueOf(primaryKeyGrpcService.snowflake());
    }
    if (Long.class.isAssignableFrom(idType)) {
      return primaryKeyGrpcService.snowflake();
    }
    throw new HibernateException(
      "Unanticipated return type [" + idType.getName() + "] for Snowflake conversion");
  }

  @Override
  public void configure(@NotNull Type type, Properties parameters,
    ServiceRegistry serviceRegistry) {
    idType = type.getReturnedClass();
  }
}