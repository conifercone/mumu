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
package com.sky.centaur.processor.kotlin.tools

import com.squareup.javapoet.ClassName
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * 对象工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
object ObjectUtil {

    @JvmStatic
    fun getFields(element: Element): List<VariableElement> {
        val fields = mutableListOf<VariableElement>()
        if (element.kind == ElementKind.CLASS) {
            for (enclosedElement in element.enclosedElements) {
                if (enclosedElement.kind == ElementKind.FIELD) {
                    fields.add(enclosedElement as VariableElement)
                }
            }
        }
        return fields
    }

    @JvmStatic
    fun getSuperclassElement(element: Element, types: Types): Optional<TypeElement> {
        if (element is TypeElement) {
            val superclassMirror: TypeMirror = element.superclass

            if (superclassMirror.kind != javax.lang.model.type.TypeKind.NONE) {
                val superclassElement = types.asElement(superclassMirror)
                if (superclassElement is TypeElement) {
                    return Optional.of(superclassElement)
                }
            }
        }
        return Optional.empty()
    }

    @JvmStatic
    fun getEntityName(fieldElement: VariableElement): String {
        return getEntityType(fieldElement).simpleName.toString()
    }

    @JvmStatic
    fun getEntityPackageName(fieldElement: VariableElement, elements: Elements): String {
        // 获取字段所属的实体类型
        val entityType = getEntityType(fieldElement)
        // 获取包名
        return entityType.let {
            val packageElement = elements.getPackageOf(it)
            packageElement.qualifiedName.toString()
        }
    }

    @JvmStatic
    fun getEntityType(fieldElement: VariableElement): TypeElement {
        // 获取字段的父元素
        val enclosingElement: Element = fieldElement.enclosingElement

        // 检查父元素是否为 TypeElement
        return if (enclosingElement is TypeElement) {
            enclosingElement
        } else {
            throw IllegalArgumentException("The provided element is not a field of a class.")
        }
    }


    @JvmStatic
    fun getFieldClassName(fieldElement: VariableElement): Optional<ClassName> {
        // 获取字段的类型
        val typeMirror: TypeMirror = fieldElement.asType()

        // 检查类型是否为 DeclaredType (即类或接口)
        if (typeMirror is DeclaredType) {
            val typeElement = typeMirror.asElement() as? TypeElement
            typeElement?.let {
                // 获取类的 ClassName
                return Optional.of(ClassName.get(it))
            }
        }

        return Optional.empty()
    }
}
