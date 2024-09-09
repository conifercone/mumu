dependencies {
    implementation(project(":mumu-log:log-domain"))
    implementation(project(":mumu-log:log-client"))
    implementation(project(":mumu-unique:unique-client"))
    implementation(project(":mumu-extension"))
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.micrometer.tracing)
}