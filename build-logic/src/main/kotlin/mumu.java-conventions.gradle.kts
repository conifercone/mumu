plugins {
    `java-library`
    id("io.freefair.lombok")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    annotationProcessor(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.grpc.dependencies))
    implementation(platform(libs.grpc.bom))
    implementation(platform(libs.protobuf.bom))
    implementation(platform(libs.guava.bom))
    implementation(platform(libs.awssdk.bom))

    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.log4j2)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.disruptor)
    implementation(libs.bundles.jackson)
    testImplementation(libs.bundles.jackson)
    implementation(libs.jspecify)
    implementation(libs.apiguardian.api)
    implementation(libs.guava)
    implementation(libs.commons.lang3)
    implementation(libs.commons.text)
    implementation(libs.commons.io)
    implementation(libs.commons.collections4)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.progressbar)
    implementation(libs.swagger.annotations.jakarta)
    implementation(libs.moneta)
    implementation(libs.jackson.datatype.moneta)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    annotationProcessor(libs.spring.boot.configuration.processor)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    testAnnotationProcessor(libs.mapstruct.processor)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
