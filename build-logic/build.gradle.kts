plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
}
dependencies {
    implementation(libs.springboot.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.lombok.gradle.plugin)
}
