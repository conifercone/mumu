plugins {
    id("mumu.base-conventions")
}

description = "Basic module"

dependencies {
    // 原 library-conventions 的公共库（basis 是唯一不适用 spring-conventions 的模块，故自行声明）
    implementation(libs.commons.text)
    implementation(libs.commons.io)

    implementation(libs.bundles.jackson)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.moneta)

    implementation(libs.moneta)
    implementation(libs.progressbar)
    implementation(libs.jakarta.validation.api)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.spring.security.core)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.security.oauth2.core)
    implementation(libs.spring.security.oauth2.jose)
    api(libs.jakarta.persistence.api)
    compileOnly(libs.spring.boot.starter.data.jpa)
    compileOnly(libs.spring.boot.starter.data.mongodb)
    annotationProcessor(libs.hibernate.processor)
    implementation(libs.spring.cloud.commons)
    implementation(libs.grpc.api)
    implementation(libs.grpc.protobuf)
    implementation(libs.micrometer.tracing)
    implementation(libs.protobuf.java)
    implementation(libs.opencsv)
    implementation(libs.libphonenumber)
    implementation(libs.opentelemetry.log4j.appender)
    implementation(libs.commons.validator)
}
