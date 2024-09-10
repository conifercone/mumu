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
package baby.mumu.extension.aspects;

import static baby.mumu.basis.constants.CommonConstants.PERCENT_SIGN;

import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.kotlin.tools.SecurityContextUtil;
import baby.mumu.log.client.api.SystemLogGrpcService;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCo;
import java.lang.reflect.Method;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 危险操作注解切面
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Aspect
public class DangerousOperationAspect {

  private final SystemLogGrpcService systemLogGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(DangerousOperationAspect.class);

  public DangerousOperationAspect(SystemLogGrpcService systemLogGrpcService) {
    this.systemLogGrpcService = systemLogGrpcService;
  }

  @Before("@within(baby.mumu.basis.annotations.DangerousOperation) || @annotation(baby.mumu.basis.annotations.DangerousOperation)")
  public void checkDangerousOperation(JoinPoint joinPoint) {
    getCurrentMethod(joinPoint).map(method -> method.getAnnotation(DangerousOperation.class))
        .or(() -> Optional.ofNullable(
            joinPoint.getTarget().getClass().getAnnotation(DangerousOperation.class))).ifPresent(
            annotationNonNull -> SecurityContextUtil.getLoginAccountId()
                .ifPresent(accountId -> {
                  String content = String.format(
                      "The user with user ID %s performed a dangerous operation:%s", accountId,
                      resolveParameters(annotationNonNull.value(), joinPoint));
                  systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
                      .setSystemLogSubmitCo(
                          SystemLogSubmitGrpcCo.newBuilder()
                              .setContent(content)
                              .setCategory("dangerousOperation")
                              .build())
                      .build());
                  LOGGER.info(content);
                }));
  }

  private String resolveParameters(@NotNull String value, @NotNull JoinPoint joinPoint) {
    String finalValue = value;
    if (value.contains(PERCENT_SIGN)) {
      finalValue = replaceParameters(finalValue, joinPoint.getArgs());
    }
    return finalValue;
  }

  private String replaceParameters(@NotNull String value, Object[] args) {
    String finalValue = value;

    if (finalValue.contains(PERCENT_SIGN) && ArrayUtils.isNotEmpty(args)) {
      for (int i = 0; i < args.length; i++) {
        if (args[i] != null) {
          finalValue = finalValue.replace(PERCENT_SIGN + i, args[i].toString());
        }
      }
    }
    return finalValue;
  }

  private Optional<Method> getCurrentMethod(@NotNull JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
    try {
      return Optional.of(joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes));
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
  }
}
