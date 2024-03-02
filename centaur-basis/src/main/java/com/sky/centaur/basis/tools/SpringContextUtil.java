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

package com.sky.centaur.basis.tools;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * spring上下文工具类
 *
 * @author 单开宇
 * @since 2024-02-05
 */
@Component
@Order(HIGHEST_PRECEDENCE)
@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public final class SpringContextUtil implements ApplicationContextAware {

  /**
   * Spring上下文
   */
  private static ApplicationContext applicationContext;

  /**
   * bean name -> bean LRU cache
   */
  private static final ConcurrentCache<String, Object> BEAN_NAME_CACHE = new ConcurrentCache<>(50);

  /**
   * bean class -> bean LRU cache
   */
  private static final ConcurrentCache<Class<?>, Object> BEAN_CLASS_CACHE = new ConcurrentCache<>(
      50);

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    if (SpringContextUtil.applicationContext == null) {
      SpringContextUtil.applicationContext = applicationContext;
    }
  }

  /**
   * 获取applicationContext
   *
   * @return applicationContext
   */
  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * 通过name获取 Bean.
   *
   * @param name bean名称
   * @return 当前bean
   */
  public static @NotNull Object getBean(@NotNull String name) {
    return BEAN_NAME_CACHE.computeIfAbsent(name, key -> getApplicationContext().getBean(name));
  }

  /**
   * 通过class获取Bean.
   *
   * @param <T>   泛型
   * @param clazz class
   * @return 当前bean
   */
  public static <T> @NotNull T getBean(@NotNull Class<T> clazz) {
    //noinspection unchecked
    return (T) BEAN_CLASS_CACHE.computeIfAbsent(clazz,
        key -> getApplicationContext().getBean(clazz));
  }

  /**
   * 通过name,以及Clazz返回指定的Bean
   *
   * @param <T>   泛型
   * @param name  bean名称
   * @param clazz class
   * @return 当前bean
   */
  public static <T> @NotNull T getBean(@NotNull String name, @NotNull Class<T> clazz) {
    return getApplicationContext().getBean(name, clazz);
  }

  /**
   * 获取clazz类型所有bean实例
   *
   * @param <T>   泛型
   * @param clazz class
   * @return Map<String, T>  当前类型所有bean
   */
  public static <T> @NotNull Map<String, T> getBeanOfType(@NotNull Class<T> clazz) {
    return getApplicationContext().getBeansOfType(clazz);
  }

  /**
   * 检查spring容器里是否有对应的bean,有则进行消费
   *
   * @param clazz    class
   * @param consumer 消费
   * @param <T>      泛型
   */
  public static <T> void getBeanThen(@NotNull Class<T> clazz, @NotNull Consumer<T> consumer) {
    String[] beanNames = getApplicationContext().getBeanNamesForType(clazz, false, false);
    if (beanNames.length == 1) {
      consumer.accept(getApplicationContext().getBean(beanNames[0], clazz));
    } else if (beanNames.length > 1) {
      consumer.accept(getApplicationContext().getBean(clazz));
    }
  }

  /**
   * 注册bean
   *
   * @param clazz bean class
   * @param name  bean name
   * @param <T>   bean class类型
   */
  public static <T> void registerBean(@NotNull Class<T> clazz, @NotNull String name) {
    ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
    DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();
    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(
        clazz);
    defaultListableBeanFactory.setAllowBeanDefinitionOverriding(true);

    defaultListableBeanFactory.registerBeanDefinition(name,
        beanDefinitionBuilder.getBeanDefinition());
    Object bean = getApplicationContext().getBean(name);
    if (BEAN_NAME_CACHE.get(name) != null) {
      BEAN_NAME_CACHE.put(name, bean);
    }
    if (BEAN_CLASS_CACHE.get(clazz) != null) {
      BEAN_CLASS_CACHE.put(clazz, bean);
    }
  }

  /**
   * 注册bean
   *
   * @param clazz bean class
   * @param <T>   bean class类型
   */
  public static <T> void registerBean(@NotNull Class<T> clazz) {
    registerBean(clazz, clazz.getName());
  }
}
