apply(from = "../scripts/springboot.gradle")

dependencies {
    implementation(project(":centaur-authentication:authentication-client"))
    implementation(project(":centaur-mail:mail-infrastructure"))
    implementation(project(":centaur-mail:mail-adapter"))
    implementation(project(":centaur-mail:mail-client"))
    implementation(project(":centaur-mail:mail-application"))
    implementation(project(":centaur-extension"))
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
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.jasypt)
    implementation(libs.swagger3Ui)
}
