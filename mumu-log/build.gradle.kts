apply(from = "../scripts/springboot.gradle")

dependencies {
    implementation(project(":mumu-authentication:authentication-client"))
    implementation(project(":mumu-log:log-infrastructure"))
    implementation(project(":mumu-log:log-application"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-log:log-adapter"))
    implementation(libs.jasypt)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.spring.kafka)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.bundles.micrometer)
    implementation(libs.caffeine)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.skyscreamer", module = "jsonassert")
    }
    testImplementation(libs.jasypt)
}
