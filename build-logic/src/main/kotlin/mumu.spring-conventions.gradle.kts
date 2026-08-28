/**
 * Spring 工程依赖底座：Spring Boot 基础、Log4j2、校验、JSON、MapStruct。
 * 原 library-conventions 的依赖因 25/30 模块共用，并入此处；mumu-basis 单独声明。
 */
plugins {
    id("mumu.base-conventions")
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.log4j2)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.disruptor)

    implementation(libs.kotlin.reflect)
    implementation(libs.swagger.annotations.jakarta)

    annotationProcessor(libs.spring.boot.configuration.processor)

    // 原 library-conventions 的公共库
    implementation(libs.commons.text)
    implementation(libs.commons.io)

    implementation(libs.bundles.jackson)
    testImplementation(libs.bundles.jackson)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.moneta)

    implementation(libs.moneta)
    implementation(libs.progressbar)
    implementation(libs.jakarta.validation.api)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    testAnnotationProcessor(libs.mapstruct.processor)
}
