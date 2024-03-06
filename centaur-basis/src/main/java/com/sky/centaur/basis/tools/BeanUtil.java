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

import com.sky.centaur.basis.dataobject.jpa.JpaBasisDataObject;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 对象工具类
 *
 * @author 单开宇
 * @since 2024-03-06
 */
public class BeanUtil {

  /**
   * 获取当前对象中属性值为null的所有属性名
   *
   * @param source 操作对象
   * @return 为null的所有属性名
   */
  public static String @NotNull [] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

    Set<String> emptyNames = new HashSet<>();
    for (java.beans.PropertyDescriptor pd : pds) {
      Object srcValue = src.getPropertyValue(pd.getName());
      if (srcValue == null) {
        emptyNames.add(pd.getName());
      }
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

  /**
   * jpa更新数据属性赋值
   *
   * @param source 更新值
   * @param target 数据库目标值
   */
  public static void jpaUpdate(JpaBasisDataObject source, JpaBasisDataObject target) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    target.setModifier(null);
    target.setModificationTime(null);
  }
}
