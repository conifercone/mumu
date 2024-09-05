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
package baby.mumu.extension.aspects;

import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.kotlin.tools.SecurityContextUtil;
import baby.mumu.log.client.api.SystemLogGrpcService;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCo;
import java.lang.reflect.Method;
import java.util.Optional;
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
 * @since 1.0.5
 */
@Aspect
public class DangerousOperationAspect {

  private final SystemLogGrpcService systemLogGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(DangerousOperationAspect.class);

  public DangerousOperationAspect(SystemLogGrpcService systemLogGrpcService) {
    this.systemLogGrpcService = systemLogGrpcService;
  }

  @Before("@within(baby.mumu.basis.annotations.DangerousOperation) || @annotation(baby.mumu.basis.annotations.DangerousOperation)")
  public void checkDangerousOperation(JoinPoint joinPoint) throws NoSuchMethodException {
    Method method = getCurrentMethod(joinPoint);
    DangerousOperation annotation = method.getAnnotation(DangerousOperation.class);

    if (annotation == null) {
      annotation = joinPoint.getTarget().getClass().getAnnotation(DangerousOperation.class);
    }
    Optional.ofNullable(annotation).ifPresent(
        annotationNonNull -> SecurityContextUtil.getLoginAccountId()
            .ifPresent(accountId -> {
              String content = String.format(
                  "The user with user ID %s performed a dangerous operation:%s", accountId,
                  annotationNonNull.value());
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

  private @NotNull Method getCurrentMethod(@NotNull JoinPoint joinPoint)
      throws NoSuchMethodException {
    String methodName = joinPoint.getSignature().getName();
    Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
    return joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes);
  }
}
