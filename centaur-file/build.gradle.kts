apply(from = "../scripts/springboot.gradle")

dependencies {
    implementation(project(":centaur-authentication:authentication-client"))
    implementation(project(":centaur-extension"))
    implementation(project(":centaur-file:file-infrastructure"))
    implementation(project(":centaur-file:file-adapter"))
    implementation(project(":centaur-file:file-client"))
    implementation(project(":centaur-file:file-application"))
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.bundles.micrometer)
    implementation(libs.grpc.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.skyscreamer", module = "jsonassert")
    }
    testImplementation(libs.jasypt)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.grpc.client.spring.boot.starter)
    testImplementation(libs.spring.web)
    implementation(libs.jasypt)
    implementation(libs.swagger3Ui)
    implementation(libs.minio)
}
