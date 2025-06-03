description = "Mail service"

dependencies {
    implementation(project(":mumu-services:mumu-authentication:authentication-client"))
    implementation(project(":mumu-services:mumu-mail:mail-infrastructure"))
    implementation(project(":mumu-services:mumu-mail:mail-adapter"))
    implementation(project(":mumu-services:mumu-mail:mail-client"))
    implementation(project(":mumu-services:mumu-mail:mail-application"))
    implementation(project(":mumu-extension"))
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.bundles.micrometer)
    implementation(libs.grpc.server.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jasypt)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.grpc.client.spring.boot.starter)
    testImplementation(libs.spring.web)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.jasypt)
    implementation(libs.swagger3Ui)
    annotationProcessor(project(":mumu-processor"))
}
