plugins {
    `java-library`
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

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring.boot.dependencies").get()))
    annotationProcessor(platform(libs.findLibrary("spring.boot.dependencies").get()))
    implementation(platform(libs.findLibrary("spring.cloud.dependencies").get()))
    implementation(platform(libs.findLibrary("spring.grpc.dependencies").get()))
    implementation(platform(libs.findLibrary("grpc.bom").get()))
    implementation(platform(libs.findLibrary("protobuf.bom").get()))
    implementation(platform(libs.findLibrary("guava.bom").get()))
    implementation(platform(libs.findLibrary("awssdk.bom").get()))

    implementation(libs.findLibrary("spring.boot.starter").get())
    implementation(libs.findLibrary("spring.boot.starter.log4j2").get())
    implementation(libs.findLibrary("spring.boot.starter.validation").get())
    implementation(libs.findLibrary("disruptor").get())
    implementation(libs.findBundle("jackson").get())
    testImplementation(libs.findBundle("jackson").get())
    implementation(libs.findLibrary("jspecify").get())
    implementation(libs.findLibrary("apiguardian.api").get())
    implementation(libs.findLibrary("guava").get())
    implementation(libs.findLibrary("commons.lang3").get())
    implementation(libs.findLibrary("commons.text").get())
    implementation(libs.findLibrary("commons.io").get())
    implementation(libs.findLibrary("commons.collections4").get())
    implementation(libs.findLibrary("jackson.module.kotlin").get())
    implementation(libs.findLibrary("kotlin.reflect").get())
    implementation(libs.findLibrary("progressbar").get())
    implementation(libs.findLibrary("swagger.annotations.jakarta").get())
    implementation(libs.findLibrary("moneta").get())
    implementation(libs.findLibrary("jackson.datatype.moneta").get())

    testImplementation(libs.findLibrary("junit.jupiter").get())
    testRuntimeOnly(libs.findLibrary("junit.platform.launcher").get())

    annotationProcessor(libs.findLibrary("spring.boot.configuration.processor").get())
    implementation(libs.findLibrary("mapstruct").get())
    annotationProcessor(libs.findLibrary("mapstruct.processor").get())
    testAnnotationProcessor(libs.findLibrary("mapstruct.processor").get())
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
