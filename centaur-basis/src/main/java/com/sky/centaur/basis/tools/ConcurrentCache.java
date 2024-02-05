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

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 并发缓存（LRU）
 * <p>满载时淘汰最近最久未使用的数据</p>
 *
 * @author 单开宇
 * @since 2023-08-22
 */
public final class ConcurrentCache<K, V> {

  private final int size;

  private final Map<K, V> eden;

  private final Map<K, V> longTerm;

  public ConcurrentCache(int size) {
    this.size = size;
    this.eden = new ConcurrentHashMap<>(size);
    this.longTerm = new WeakHashMap<>(size);
  }

  /**
   * 从缓存中检索与给定键关联的值
   *
   * @param k 要检索的值的键
   * @return 与给定键关联的值，如果缓存中不存在该键，则为null
   */
  public V get(K k) {
    V v = this.eden.get(k);
    if (v == null) {
      synchronized (longTerm) {
        v = this.longTerm.get(k);
      }
      if (v != null) {
        this.eden.put(k, v);
      }
    }
    return v;
  }

  /**
   * 将给定的键值对存储到缓存中
   *
   * @param k 要存储的键
   * @param v 要存储的值
   */
  public void put(K k, V v) {
    if (this.eden.size() >= size) {
      synchronized (longTerm) {
        this.longTerm.putAll(this.eden);
      }
      this.eden.clear();
    }
    this.eden.put(k, v);
  }

  /**
   * 如果缓存中不存在给定键的值，则根据给定的键使用指定的映射函数计算并存储一个值
   *
   * @param key             要进行计算的键
   * @param mappingFunction 对缓存中不存在的键进行计算的映射函数
   * @return 缓存中与给定键关联的值；如果计算结果为 null，则返回 null
   */
  public V computeIfAbsent(K key,
      Function<? super K, ? extends V> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    V v;
    if ((v = get(key)) == null) {
      V newValue;
      if ((newValue = mappingFunction.apply(key)) != null) {
        put(key, newValue);
        return newValue;
      }
    }
    return v;
  }
}
