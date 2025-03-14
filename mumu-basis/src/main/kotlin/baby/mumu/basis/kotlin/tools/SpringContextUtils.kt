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
package baby.mumu.basis.kotlin.tools

import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import java.util.*

/**
 * spring上下文工具类
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
class SpringContextUtils : ApplicationContextAware {

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        if (Companion.applicationContext == null) {
            Companion.applicationContext = applicationContext
        }
    }

    companion object {
        /**
         * Spring上下文
         */
        private var applicationContext: ApplicationContext? = null
        private val logger = LoggerFactory.getLogger(SpringContextUtils::class.java)

        /**
         * bean class -> bean LRU cache
         */
        private val BEAN_CLASS_CACHE =
            ConcurrentCache<Class<*>, Any>(
                50
            )

        @JvmStatic
        fun getApplicationContext(): ApplicationContext? {
            return applicationContext
        }

        /**
         * 通过class获取Bean.
         *
         * @param <T>   泛型
         * @param clazz class
         * @return 当前bean，包装为 Optional
         */
        @JvmStatic
        fun <T : Any> getBean(clazz: Class<T>): Optional<T> {
            return try {
                // 使用 computeIfAbsent，获取不到的情况通过异常处理
                val bean = BEAN_CLASS_CACHE.computeIfAbsent(clazz) {
                    getApplicationContext()?.getBean(clazz) ?: throw NoSuchBeanDefinitionException(
                        clazz
                    )
                }
                @Suppress("UNCHECKED_CAST")
                Optional.of(bean as T)
            } catch (e: Exception) {
                logger.error(e.message, e)
                Optional.empty()
            }
        }
    }
}
