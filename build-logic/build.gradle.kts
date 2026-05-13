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
    implementation("org.jetbrains.kotlin:kotlin-allopen:${libs.versions.kotlinPluginVersion.get()}")
    implementation("org.jetbrains.kotlin:kotlin-noarg:${libs.versions.kotlinPluginVersion.get()}")
}
