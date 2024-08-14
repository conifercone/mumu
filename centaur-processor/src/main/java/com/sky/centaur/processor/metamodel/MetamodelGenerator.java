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
package com.sky.centaur.processor.metamodel;

import com.google.auto.service.AutoService;
import com.sky.centaur.basis.annotations.CustomDescription;
import com.sky.centaur.basis.annotations.GenerateDescription;
import com.sky.centaur.processor.kotlin.tools.ObjectUtil;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import jakarta.annotation.Generated;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

/**
 * 元模型生成器
 *
 * @author kaiyu.shan
 * @since 1.0.4
 */
@SuppressWarnings("unused")
@SupportedAnnotationTypes(
    value = {"com.sky.centaur.basis.annotations.GenerateDescription"}
)
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class MetamodelGenerator extends AbstractProcessor {

  private Messager messager;
  private Elements elementUtils;
  private Types typeUtils;
  private static final String GENERATE_DESCRIPTION_CLASS_SUFFIX = "4Desc";

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    messager = processingEnv.getMessager();
    messager.printMessage(Diagnostic.Kind.NOTE, "🎉 Centaur Entity Metamodel Generator");
  }

  @Override
  public boolean process(@NotNull Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {
    if (annotations.isEmpty() || roundEnv.processingOver()) {
      // Allow other processors to run
      return false;
    }
    Set<? extends Element> metamodelCandidates = roundEnv.getElementsAnnotatedWith(
        GenerateDescription.class);
    metamodelCandidates.stream().filter(ae -> ae.getKind() == ElementKind.CLASS).forEach(ae -> {
      try {
        generateMetaModelClass(ae);
      } catch (IOException e) {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Cannot generate metamodel class for " + ae.getClass().getName() + " because "
                + e.getMessage());
      }
    });
    return true;
  }

  void generateMetaModelClass(final @NotNull Element annotatedElement)
      throws IOException {
    String qualifiedName = annotatedElement.asType().toString();

    final String entityName;
    final String packageName;
    final String genEntityName;

    PackageElement packageElement = elementUtils.getPackageOf(annotatedElement);
    if (packageElement.isUnnamed()) {
      messager.printMessage(Diagnostic.Kind.WARNING,
          "Class " + annotatedElement.getSimpleName() + " has an unnamed package.");
      packageName = "";
      entityName = qualifiedName;
    } else {
      packageName = packageElement.getQualifiedName().toString();
      entityName = annotatedElement.getSimpleName().toString();
    }

    genEntityName = entityName + GENERATE_DESCRIPTION_CLASS_SUFFIX;

    String qualifiedGenEntityName =
        (packageName.isEmpty() ? "" : packageName + ".") + genEntityName;
    messager.printMessage(Diagnostic.Kind.NOTE,
        "Generating Entity Metamodel: " + qualifiedGenEntityName);
    Builder builder = TypeSpec.classBuilder(genEntityName)
        .addModifiers(Modifier.PUBLIC)
        .addModifiers(Modifier.ABSTRACT);
    List<VariableElement> fields = ObjectUtil.getFields(annotatedElement);
    Set<String> collect = fields.stream()
        .map(variableElement -> variableElement.getSimpleName().toString()).collect(
            Collectors.toSet());
    ObjectUtil.getSuperclassElement(annotatedElement, typeUtils)
        .ifPresent(superClassElement -> {
          List<VariableElement> superClassFields = ObjectUtil.getFields(superClassElement);
          superClassFields.forEach(superClassField -> {
            if (ObjectUtil.hasGetterSetter(superClassField, typeUtils) && !collect.contains(
                superClassField.getSimpleName().toString())) {
              fields.add(superClassField);
            }
          });
        });
    if (!CollectionUtils.isEmpty(fields)) {
      fields.stream()
          .filter(field -> field.getModifiers().contains(Modifier.PUBLIC) || (
              ObjectUtil.hasGetterSetter(field, typeUtils) || (field.getAnnotation(
                  Getter.class) != null && field.getAnnotation(
                  Setter.class) != null) || annotatedElement.getAnnotation(Data.class) != null | (
                  annotatedElement.getAnnotation(
                      Getter.class) != null && annotatedElement.getAnnotation(
                      Setter.class) != null)))
          .forEach(field -> {
            FieldSpec fieldSpec = FieldSpec.builder(String.class, field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", field.getSimpleName().toString())
                .addJavadoc(String.format(
                    "@see %s.%s#%s",
                    packageName, entityName, field.getSimpleName()))
                .build();
            builder.addField(fieldSpec);
          });
    }
    if (annotatedElement.getAnnotation(GenerateDescription.class) != null) {
      GenerateDescription generateDescription = annotatedElement.getAnnotation(
          GenerateDescription.class);
      CustomDescription[] customs = generateDescription.customs();
      for (CustomDescription custom : customs) {
        FieldSpec fieldSpec = FieldSpec.builder(String.class, custom.name())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", custom.value())
            .build();
        builder.addField(fieldSpec);
      }
    }
    builder.addAnnotation(AnnotationSpec.builder(Generated.class)
        .addMember("value", "$S", this.getClass().getName())
        .addMember("date", "$S", LocalDateTime.now().toString())
        .build());
    String author = getGitUserName().map(
        gitUserName -> getGitEmail().map(gitEmail -> String.format("%s<%s>", gitUserName, gitEmail))
            .orElse("")).orElse("");
    builder.addJavadoc(
        "The current class is automatically generated, please do not modify it.\n"
            + (StringUtils.isNotBlank(author) ? String.format(
            "@author %s\n", author) : "") + String.format("@see %s.%s", packageName, entityName));
    JavaFile javaFile = JavaFile
        .builder(packageName, builder.build())
        .build();
    JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(qualifiedGenEntityName);
    Writer writer = builderFile.openWriter();
    javaFile.writeTo(writer);
    writer.close();
  }

  private Optional<String> getGitEmail() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("git", "config", "user.email");
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String email = reader.readLine();
      process.waitFor();
      return Optional.of(email);
    } catch (IOException | InterruptedException e) {
      return Optional.empty();
    }
  }

  private Optional<String> getGitUserName() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("git", "config", "user.name");
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String userName = reader.readLine();
      process.waitFor();
      return Optional.of(userName);
    } catch (IOException | InterruptedException e) {
      return Optional.empty();
    }
  }
}
