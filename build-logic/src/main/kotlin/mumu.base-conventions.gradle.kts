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
    // 仅导入 BOM 用于版本管理，不引入具体依赖
    implementation(platform(libs.spring.boot.dependencies))
    annotationProcessor(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.grpc.dependencies))
    implementation(platform(libs.grpc.bom))
    implementation(platform(libs.protobuf.bom))
    implementation(platform(libs.guava.bom))
    implementation(platform(libs.awssdk.bom))

    implementation(libs.jspecify)
    implementation(libs.apiguardian.api)
    implementation(libs.jakarta.annotation.api)
    implementation(libs.commons.lang3)
    implementation(libs.commons.collections4)
    implementation(libs.guava)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
