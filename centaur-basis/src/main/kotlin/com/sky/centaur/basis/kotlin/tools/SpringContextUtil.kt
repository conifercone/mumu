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
package com.sky.centaur.basis.kotlin.tools

import org.springframework.beans.BeansException
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import java.util.function.Consumer

/**
 * spring上下文工具类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Suppress("unused")
class SpringContextUtil : ApplicationContextAware {
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

        /**
         * bean name -> bean LRU cache
         */
        private val BEAN_NAME_CACHE =
            ConcurrentCache<String, Any>(50)

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
         * 通过name获取 Bean.
         *
         * @param name bean名称
         * @return 当前bean
         */
        @JvmStatic
        fun getBean(name: String): Any? {
            return BEAN_NAME_CACHE.computeIfAbsent(name) {
                getApplicationContext()!!.getBean(name)
            }
        }

        /**
         * 通过class获取Bean.
         *
         * @param <T>   泛型
         * @param clazz class
         * @return 当前bean
         */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> getBean(clazz: Class<T>): T {
            return BEAN_CLASS_CACHE.computeIfAbsent(
                clazz
            ) { getApplicationContext()!!.getBean(clazz) } as T
        }

        /**
         * 通过name,以及Clazz返回指定的Bean
         *
         * @param <T>   泛型
         * @param name  bean名称
         * @param clazz class
         * @return 当前bean
         */
        @JvmStatic
        fun <T> getBean(name: String, clazz: Class<T>): T {
            return getApplicationContext()!!.getBean(name, clazz)
        }

        /**
         * 获取clazz类型所有bean实例
         *
         * @param <T>   泛型
         * @param clazz class
         * @return Map<String></String>, T>  当前类型所有bean
         */
        @JvmStatic
        fun <T> getBeanOfType(clazz: Class<T>): Map<String, T> {
            return getApplicationContext()!!.getBeansOfType(clazz)
        }

        /**
         * 检查spring容器里是否有对应的bean,有则进行消费
         *
         * @param clazz    class
         * @param consumer 消费
         * @param <T>      泛型
         */
        @JvmStatic
        fun <T> getBeanThen(clazz: Class<T>, consumer: Consumer<T>) {
            val beanNames: Array<String> =
                getApplicationContext()!!.getBeanNamesForType(clazz, false, false)
            if (beanNames.size == 1) {
                consumer.accept(
                    getApplicationContext()!!.getBean(beanNames[0], clazz)
                )
            } else if (beanNames.size > 1) {
                consumer.accept(getApplicationContext()!!.getBean(clazz))
            }
        }

        /**
         * 注册bean
         *
         * @param clazz bean class
         * @param name  bean name
         * @param <T>   bean class类型
         */
        @JvmStatic
        fun <T> registerBean(clazz: Class<T>, name: String) {
            val configurableApplicationContext =
                applicationContext as ConfigurableApplicationContext?
            val defaultListableBeanFactory =
                configurableApplicationContext!!.autowireCapableBeanFactory as DefaultListableBeanFactory
            val beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(
                clazz
            )
            defaultListableBeanFactory.isAllowBeanDefinitionOverriding = true

            defaultListableBeanFactory.registerBeanDefinition(
                name,
                beanDefinitionBuilder.beanDefinition
            )
            val bean: Any = getApplicationContext()!!.getBean(name)
            if (BEAN_NAME_CACHE[name] != null) {
                BEAN_NAME_CACHE.put(name, bean)
            }
            if (BEAN_CLASS_CACHE[clazz] != null) {
                BEAN_CLASS_CACHE.put(clazz, bean)
            }
        }

        /**
         * 注册bean
         *
         * @param clazz bean class
         * @param <T>   bean class类型
         */
        @JvmStatic
        fun <T> registerBean(clazz: Class<T>) {
            registerBean(clazz, clazz.name)
        }
    }
}
