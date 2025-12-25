description = "Log Management Service"

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-services:mumu-log:log-infra"))
    implementation(project(":mumu-services:mumu-log:log-application"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-log:log-adapter"))
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.grpc.server.spring.boot.starter)
    implementation(libs.bundles.web)
    implementation(libs.caffeine)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    annotationProcessor(project(":mumu-processor"))
}
