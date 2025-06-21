description = "File service"

dependencies {
    implementation(project(":mumu-services:mumu-authentication:authentication-client"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-storage:storage-infrastructure"))
    implementation(project(":mumu-services:mumu-storage:storage-adapter"))
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-services:mumu-storage:storage-application"))
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.bundles.micrometer)
    implementation(libs.grpc.server.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jasypt)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.grpc.client.spring.boot.starter)
    testImplementation(libs.spring.web)
    implementation(libs.jasypt)
    implementation(libs.swagger3Ui)
    implementation(libs.minio)
    annotationProcessor(project(":mumu-processor"))
}
