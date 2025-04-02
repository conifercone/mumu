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
package baby.mumu.processor.kotlin.tools

import com.palantir.javapoet.ClassName
import com.palantir.javapoet.ParameterizedTypeName
import com.palantir.javapoet.TypeName
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * 对象工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
object ObjectUtils {

    /**
     * 获取所有字段
     */
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
    fun getAllSuperclasses(element: Element, types: Types): List<TypeElement> {
        val superclasses = mutableListOf<TypeElement>()
        var currentElement: TypeElement? = element as? TypeElement
        while (currentElement != null) {
            superclasses.add(currentElement)
            val superclassMirror: TypeMirror = currentElement.superclass

            if (superclassMirror.kind == TypeKind.NONE) {
                break
            }
            val superclassElement = types.asElement(superclassMirror)
            currentElement = superclassElement as? TypeElement
        }
        return superclasses
    }


    @JvmStatic
    fun getEntityQualifiedName(fieldElement: VariableElement): String {
        return getEntityType(fieldElement).qualifiedName.toString()
    }

    @JvmStatic
    fun getEntityType(fieldElement: VariableElement): TypeElement {
        // 获取字段的父元素
        val enclosingElement: Element = fieldElement.enclosingElement

        // 检查父元素是否为 TypeElement
        return enclosingElement as? TypeElement
            ?: throw IllegalArgumentException("The provided element is not a field of a class.")
    }


    @JvmStatic
    fun getFieldClassName(
        fieldElement: VariableElement,
        elements: Elements,
        types: Types
    ): Optional<TypeName> {
        // 获取字段的类型
        val typeMirror: TypeMirror = fieldElement.asType()

        // 检查类型是否为 DeclaredType (即类或接口)
        if (typeMirror is DeclaredType) {
            val typeElement = typeMirror.asElement()
            // 处理泛型参数
            val className = ClassName.get(typeElement as TypeElement)
            val typeArguments = typeMirror.typeArguments.map { getTypeName(it, elements, types) }

            return if (typeArguments.isNotEmpty()) {
                Optional.of(ParameterizedTypeName.get(className, *typeArguments.toTypedArray()))
            } else {
                Optional.of(className)
            }
        }
        return Optional.empty()
    }

    @JvmStatic
    fun getTypeName(typeMirror: TypeMirror, elements: Elements, types: Types): TypeName {
        return when (typeMirror.kind) {
            TypeKind.DECLARED -> {
                val declaredType = typeMirror as DeclaredType
                val typeElement = declaredType.asElement() as TypeElement
                val className = ClassName.get(typeElement)

                val typeArguments =
                    declaredType.typeArguments.map { getTypeName(it, elements, types) }
                if (typeArguments.isNotEmpty()) {
                    ParameterizedTypeName.get(className, *typeArguments.toTypedArray())
                } else {
                    className
                }
            }

            TypeKind.ARRAY -> {
                getTypeName(
                    (typeMirror as javax.lang.model.type.ArrayType).componentType,
                    elements,
                    types
                )
                TypeName.get(typeMirror) as TypeName
            }

            else -> TypeName.get(typeMirror)
        }
    }
}
