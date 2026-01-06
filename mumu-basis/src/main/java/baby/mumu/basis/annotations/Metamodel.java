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

package baby.mumu.basis.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 元模型注解
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface Metamodel {

    Meta[] customs() default {};

    /**
     * 是否生成项目版本号
     *
     * @since 2.2.0
     */
    boolean projectVersion() default false;

    /**
     * 项目版本号字段名
     *
     * @since 2.2.0
     */
    String projectVersionFiledName() default "PROJECT_VERSION";

    /**
     * 是否生成格式化后的项目版本号
     *
     * @since 2.3.0
     */
    boolean formattedProjectVersion() default false;

    /**
     * 格式化后的项目版本号字段名
     *
     * @since 2.3.0
     */
    String formattedProjectVersionFiledName() default "FORMATTED_PROJECT_VERSION";

    /**
     * 是否生成项目名
     *
     * @since 2.2.0
     */
    boolean projectName() default false;

    /**
     * 项目名字段名
     *
     * @since 2.2.0
     */
    String projectNameFiledName() default "PROJECT_NAME";
}
