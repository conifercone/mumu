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
package baby.mumu.extension.aspects;

import baby.mumu.basis.annotations.Conditional;
import baby.mumu.basis.condition.ConditionalExecutor;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 条件注解切面
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
@Aspect
public class ConditionalAspect extends AbstractAspect {

  private final ApplicationContext applicationContext;
  private static final Logger log = LoggerFactory.getLogger(ConditionalAspect.class);

  public ConditionalAspect(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Around("@annotation(baby.mumu.basis.annotations.Conditional)")
  public Object rounding(ProceedingJoinPoint joinPoint) throws Throwable {
    return Optional.ofNullable(getMethodAnnotation(joinPoint, Conditional.class))
      .map(conditional -> ConditionalExecutor.of(
          applicationContext.getBean(conditional.value()).matches())
        .orElseGet(() -> {
          try {
            return joinPoint.proceed();
          } catch (Throwable e) {
            throw new RuntimeException(e);
          }
        }, () -> {
          log.warn("{} method execution conditions are not met",
            joinPoint.getSignature().getName());
          return null;
        })).orElse(joinPoint.proceed());
  }
}
