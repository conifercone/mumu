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

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

/**
 * 并发缓存（LRU）
 *
 * 满载时淘汰最近最久未使用的数据
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
class ConcurrentCache<K, V>(private val size: Int) {
    private val eden: MutableMap<K, V> = ConcurrentHashMap(size)

    private val longTerm: MutableMap<K, V> = WeakHashMap(size)

    /**
     * 从缓存中检索与给定键关联的值
     *
     * @param k 要检索的值的键
     * @return 与给定键关联的值，如果缓存中不存在该键，则为null
     */
    operator fun get(k: K): V? {
        var v = eden[k]
        if (v == null) {
            var tempValue: V?
            synchronized(longTerm) {
                tempValue = longTerm[k]
            }
            v = tempValue
            if (v != null) {
                eden[k] = v
            }
        }
        return v
    }

    /**
     * 将给定的键值对存储到缓存中
     *
     * @param k 要存储的键
     * @param v 要存储的值
     */
    fun put(k: K, v: V) {
        if (eden.size >= size) {
            synchronized(longTerm) {
                longTerm.putAll(this.eden)
            }
            eden.clear()
        }
        eden[k] = v
    }

    /**
     * 如果缓存中不存在给定键的值，则根据给定的键使用指定的映射函数计算并存储一个值
     *
     * @param key             要进行计算的键
     * @param mappingFunction 对缓存中不存在的键进行计算的映射函数
     * @return 缓存中与给定键关联的值；如果计算结果为 null，则返回 null
     */
    fun computeIfAbsent(
        key: K,
        mappingFunction: Function<in K, out V>
    ): V? {
        Objects.requireNonNull(mappingFunction)
        var v: V?
        if (get(key).also { v = it } == null) {
            var newValue: V
            if (mappingFunction.apply(key).also { newValue = it } != null) {
                put(key, newValue)
                return newValue
            }
        }
        return v
    }
}
