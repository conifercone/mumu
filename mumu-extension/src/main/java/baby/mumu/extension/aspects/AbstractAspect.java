/*
 * Copyright (c) 2024-2026, the original author or authors.
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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 切面抽象类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
public abstract class AbstractAspect {

    protected <T extends Annotation> T getMethodAnnotation(@NonNull JoinPoint joinPoint,
                                                           Class<T> clazz) throws NoSuchMethodException {
        T annotation = null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?>[] paramTypeArray = methodSignature.getParameterTypes();
        Method transferMoney = joinPoint.getTarget().getClass()
            .getDeclaredMethod(methodSignature.getName(), paramTypeArray);
        boolean annotationPresent = transferMoney.isAnnotationPresent(clazz);
        if (annotationPresent) {
            annotation = transferMoney.getAnnotation(clazz);
        }
        return annotation;
    }

    protected Optional<Method> getCurrentMethod(@NonNull JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
        try {
            return Optional.of(joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

}
