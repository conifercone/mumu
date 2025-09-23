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

package baby.mumu.processor.metamodel;

import baby.mumu.basis.annotations.Meta;
import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.processor.kotlin.tools.ObjectUtils;
import com.google.auto.service.AutoService;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.TypeSpec.Builder;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
import javax.annotation.processing.SupportedOptions;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

/**
 * 元模型生成器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@SuppressWarnings("unused")
@SupportedAnnotationTypes(
  value = {"baby.mumu.basis.annotations.Metamodel"}
)
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@AutoService(Processor.class)
@SupportedOptions({"gradle.version", "os.name", "java.version", "project.version", "project.name"})
public class MetamodelGenerator extends AbstractProcessor {

  public static final String SEE_S_S_LINK_S = "@see %s.%s {@link %s}";
  private Messager messager;
  private Elements elementUtils;
  private Types typeUtils;
  private String authorName;
  private String authorEmail;
  private static final String GENERATE_DESCRIPTION_CLASS_SUFFIX = "Metamodel";
  private static final String SINGULAR_FIELD_SUFFIX = "_SINGULAR";
  private String gradleVersion;
  private String javaVersion;
  private String osName;
  private String projectVersion;
  private String projectName;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    messager = processingEnv.getMessager();
    authorName = getGitUserName().orElse(StringUtils.EMPTY);
    authorEmail = getGitEmail().orElse(StringUtils.EMPTY);
    gradleVersion = processingEnv.getOptions().get("gradle.version");
    javaVersion = processingEnv.getOptions().get("java.version");
    osName = processingEnv.getOptions().get("os.name");
    projectVersion = processingEnv.getOptions().get("project.version");
    projectName = processingEnv.getOptions().get("project.name");
    messager.printMessage(Diagnostic.Kind.NOTE, "🥐 MuMu Entity Metamodel Generator");
  }

  @Override
  public boolean process(@NonNull Set<? extends TypeElement> annotations,
    RoundEnvironment roundEnv) {
    if (annotations.isEmpty() || roundEnv.processingOver()) {
      // Allow other processors to run
      return false;
    }
    Set<? extends Element> metamodelCandidates = roundEnv.getElementsAnnotatedWith(
      Metamodel.class);
    metamodelCandidates.stream().filter(ae -> ae.getKind() == ElementKind.CLASS).forEach(ae -> {
      try {
        generateMetaModelClass(ae);
      } catch (IOException e) {
        messager.printMessage(Diagnostic.Kind.ERROR,
          "🤔 Cannot generate metamodel class for " + ae.getClass().getName() + " because "
            + e.getMessage());
      }
    });
    return true;
  }

  void generateMetaModelClass(final @NonNull Element annotatedElement)
    throws IOException {
    String qualifiedName = annotatedElement.asType().toString();

    final String entityName;
    final String packageName;
    final String genEntityName;

    PackageElement packageElement = elementUtils.getPackageOf(annotatedElement);
    if (packageElement.isUnnamed()) {
      messager.printMessage(Diagnostic.Kind.WARNING,
        "🤔 Class " + annotatedElement.getSimpleName() + " has an unnamed package.");
      packageName = StringUtils.EMPTY;
      entityName = qualifiedName;
    } else {
      packageName = packageElement.getQualifiedName().toString();
      entityName = annotatedElement.getSimpleName().toString();
    }
    genEntityName = entityName + MetamodelGenerator.GENERATE_DESCRIPTION_CLASS_SUFFIX;

    String qualifiedGenEntityName =
      (packageName.isEmpty() ? StringUtils.EMPTY : packageName + ".") + genEntityName;
    messager.printMessage(Diagnostic.Kind.NOTE,
      "🥐 Generating Entity Metamodel: " + qualifiedGenEntityName);
    Builder builder = TypeSpec.classBuilder(genEntityName)
      .addModifiers(Modifier.PUBLIC)
      .addModifiers(Modifier.ABSTRACT);
    generateFields(annotatedElement, packageName, entityName, builder);
    OffsetDateTime dateTime = OffsetDateTime.now(ZoneOffset.UTC);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    String formattedDateTime = dateTime.format(formatter);
    builder.addAnnotation(AnnotationSpec.builder(Generated.class)
      .addMember("value", "$S", this.getClass().getName())
      .addMember("date", "$S", formattedDateTime)
      .addMember("comments", "$S",
        String.format("compiler from gradle %s, environment: Java %s OS %s",
          gradleVersion,
          javaVersion,
          osName))
      .build());
    builder.addJavadoc(
      "The current class is automatically generated, please do not modify it.\n\n"
        + (StringUtils.isNotBlank(authorName) && StringUtils.isNotBlank(authorEmail)
        ? String.format(
        "@author <a href=\"mailto:%s\">%s</a>%n", authorEmail, authorName) : StringUtils.EMPTY)
        + String.format(
        "@see %s.%s%n", packageName, entityName) + String.format(
        "@since %s", projectVersion));
    JavaFile javaFile = JavaFile
      .builder(packageName, builder.build())
      .build();
    javaFile.writeTo(processingEnv.getFiler());
  }

  private void generateFields(@NonNull Element annotatedElement, String packageName,
    String entityName,
    Builder builder) {
    Metamodel annotation = annotatedElement.getAnnotation(Metamodel.class);
    generateBasicProjectInformation(packageName, entityName, builder, annotation);
    List<VariableElement> fields = ObjectUtils.getFields(annotatedElement);
    Set<String> collect = fields.stream()
      .map(variableElement -> variableElement.getSimpleName().toString()).collect(
        Collectors.toSet());
    List<VariableElement> superClassFields = ObjectUtils.getAllSuperclasses(annotatedElement,
        typeUtils)
      .stream()
      .flatMap(typeElement -> ObjectUtils.getFields(typeElement).stream())
      .collect(
        Collectors.toMap(VariableElement::getSimpleName, variableElement -> variableElement,
          (existing, replacement) -> existing)).values()
      .stream()
      .toList();
    superClassFields.forEach(superClassField -> {
      if (!collect.contains(superClassField.getSimpleName().toString())) {
        fields.add(superClassField);
      }
    });
    if (CollectionUtils.isNotEmpty(fields)) {
      fields.forEach(field -> {
        FieldSpec fieldSpec = FieldSpec.builder(String.class,
            camelToUpperUnderscore(field.getSimpleName().toString()))
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
          .initializer("$S", field.getSimpleName().toString())
          .addJavadoc(String.format(
            "@see %s#%s",
            ObjectUtils.getEntityQualifiedName(field), field.getSimpleName()))
          .build();
        builder.addField(fieldSpec);
        ObjectUtils.getFieldClassName(field, elementUtils, typeUtils).ifPresent(fieldClassName -> {
          FieldSpec fieldSingularSpec = FieldSpec.builder(
              ParameterizedTypeName.get(ClassName.get(SingularAttribute.class.getPackageName(),
                  SingularAttribute.class.getSimpleName()),
                TypeName.get(ObjectUtils.getEntityType(field).asType()),
                fieldClassName),
              camelToUpperUnderscore(field.getSimpleName().toString())
                .concat(MetamodelGenerator.SINGULAR_FIELD_SUFFIX))
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.VOLATILE)
            .addJavadoc(String.format(
              "@see %s#%s",
              ObjectUtils.getEntityQualifiedName(field), field.getSimpleName()))
            .build();
          builder.addField(fieldSingularSpec);
        });
      });
    }
    Metamodel metamodel = annotatedElement.getAnnotation(
      Metamodel.class);
    Meta[] customs = metamodel.customs();
    for (Meta custom : customs) {
      FieldSpec fieldSpec = FieldSpec.builder(String.class, custom.name())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", custom.value())
        .addJavadoc(String.format(
          MetamodelGenerator.SEE_S_S_LINK_S,
          packageName, entityName, Metamodel.class.getName()))
        .build();
      builder.addField(fieldSpec);
    }
  }

  /**
   * 属性名转换为大写下划线格式
   *
   * @param input 输入字符
   * @return 转换后字符
   */
  private String camelToUpperUnderscore(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isUpperCase(c) && i > 0) {
        result.append('_');
      }
      result.append(Character.toUpperCase(c));
    }
    return result.toString();
  }

  private void generateBasicProjectInformation(String packageName, String entityName,
    Builder builder,
    @NonNull Metamodel annotation) {
    String seeDoc = String.format(
      MetamodelGenerator.SEE_S_S_LINK_S,
      packageName, entityName, Metamodel.class.getName());
    if (annotation.projectVersion()) {
      FieldSpec fieldSpec = FieldSpec.builder(String.class, annotation.projectVersionFiledName())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", projectVersion)
        .addJavadoc(seeDoc)
        .build();
      builder.addField(fieldSpec);
    }
    if (annotation.formattedProjectVersion()) {
      FieldSpec fieldSpec = FieldSpec.builder(String.class,
          annotation.formattedProjectVersionFiledName())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", String.format("(v%s)", projectVersion))
        .addJavadoc(seeDoc)
        .build();
      builder.addField(fieldSpec);
    }
    if (annotation.projectName()) {
      FieldSpec fieldSpec = FieldSpec.builder(String.class, annotation.projectNameFiledName())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", projectName)
        .addJavadoc(seeDoc)
        .build();
      builder.addField(fieldSpec);
    }
  }

  /**
   * 获取git user邮箱地址
   *
   * @return git user邮箱地址
   */
  private Optional<String> getGitEmail() {
    if (gitNotAvailable()) {
      return Optional.empty();
    }
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("git", "config", "user.email");
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String email = reader.readLine();
      process.waitFor();
      return Optional.ofNullable(email);
    } catch (IOException e) {
      return Optional.empty();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return Optional.empty();
    }
  }

  /**
   * 获取git user 用户名
   *
   * @return git user 用户名
   */
  private Optional<String> getGitUserName() {
    if (gitNotAvailable()) {
      return Optional.empty();
    }
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("git", "config", "user.name");
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String userName = reader.readLine();
      process.waitFor();
      return Optional.ofNullable(userName);
    } catch (IOException e) {
      return Optional.empty();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return Optional.empty();
    }
  }

  private boolean gitNotAvailable() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("git", "--version");
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      int exitCode = process.waitFor();
      return exitCode != 0;
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      return true;
    }
  }
}
