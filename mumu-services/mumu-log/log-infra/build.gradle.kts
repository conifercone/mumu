dependencies {
    implementation(project(":mumu-services:mumu-log:log-domain"))
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-extension"))
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.micrometer.tracing)
}
