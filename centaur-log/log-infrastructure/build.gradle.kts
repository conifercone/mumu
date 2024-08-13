dependencies {
    implementation(project(":centaur-log:log-domain"))
    implementation(project(":centaur-log:log-client"))
    implementation(project(":centaur-unique:unique-client"))
    implementation(project(":centaur-extension"))
    annotationProcessor(project(":centaur-processor"))
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.micrometer.tracing)
}
