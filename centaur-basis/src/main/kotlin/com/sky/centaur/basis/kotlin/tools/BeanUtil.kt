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

import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import java.io.Serializable
import java.lang.invoke.SerializedLambda
import java.util.*

/**
 * 对象工具类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
object BeanUtil {
    /**
     * 获取当前对象中属性值为null的所有属性名
     *
     * @param source 操作对象
     * @return 为null的所有属性名
     */
    @JvmStatic
    fun getNullPropertyNames(source: Any?): Array<String> {
        val src: BeanWrapper = BeanWrapperImpl(source!!)
        val pds = src.propertyDescriptors

        val emptyNames: MutableSet<String> = HashSet()
        for (pd in pds) {
            val srcValue = src.getPropertyValue(pd.name)
            if (srcValue == null) {
                emptyNames.add(pd.name)
            }
        }
        return emptyNames.toTypedArray()
    }

    @FunctionalInterface
    interface GetterFunction<T, R> : (T) -> R, Serializable

    @JvmStatic
    fun <T> getPropertyName(getter: GetterFunction<T, *>): String {
        try {
            val writeReplace = getter.javaClass.getDeclaredMethod("writeReplace")
            writeReplace.isAccessible = true
            val serializedLambda = writeReplace.invoke(getter) as SerializedLambda

            val methodName = serializedLambda.implMethodName

            val propertyName: String = when {
                methodName.startsWith("get") -> methodName.substring(3)
                methodName.startsWith("is") -> methodName.substring(2)
                else -> throw IllegalArgumentException("Provided method is not a valid getter method")
            }

            return propertyName.substring(0, 1)
                .lowercase(Locale.getDefault()) + propertyName.substring(1)
        } catch (e: Exception) {
            throw RuntimeException("Failed to extract property name from getter method", e)
        }
    }
}
