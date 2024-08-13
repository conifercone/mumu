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
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types

/**
 * 对象工具类
 *
 * @author kaiyu.shan
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
    fun hasGetterSetter(element: Element, types: Types): Boolean {
        if (element.kind != ElementKind.FIELD) {
            return false
        }
        val field = element as VariableElement
        val fieldName = field.simpleName.toString()
        val typeElement = types.asElement(field.enclosingElement.asType()) as TypeElement
        // Check for getter
        val hasGetter = hasMethod(typeElement, "get${fieldName.capitalize()}")
        // Check for setter
        val hasSetter = hasMethod(typeElement, "set${fieldName.capitalize()}")
        return hasGetter && hasSetter
    }

    private fun hasMethod(
        typeElement: TypeElement,
        methodName: String
    ): Boolean {
        val enclosedElements = typeElement.enclosedElements

        return enclosedElements.any { element ->
            element.kind == ElementKind.METHOD && element is ExecutableElement && element.simpleName.toString() == methodName
        }
    }

    private fun String.capitalize(): String {
        return if (isNotEmpty()) {
            this[0].uppercaseChar() + substring(1)
        } else {
            this
        }
    }
}
