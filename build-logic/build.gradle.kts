plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    // 允许 src/main/kotlin 中的预编译脚本插件访问类型安全的 libs 访问器
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.springboot.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.lombok.gradle.plugin)
    implementation(libs.kotlin.allopen)
    implementation(libs.kotlin.noarg)
    implementation(libs.protobuf.gradle.plugin)
}
