dependencies {
    implementation(project(":centaur-log:log-domain"))
    implementation(project(":centaur-log:log-client"))
    implementation(project(":centaur-unique:unique-client"))
    implementation(project(":centaur-extension"))
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.micrometer.tracing)
}
