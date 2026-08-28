plugins {
    id("mumu.springboot-conventions")
}

description = "Log Management Service"

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-services:mumu-log:log-infra"))
    implementation(project(":mumu-services:mumu-log:log-application"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-log:log-adapter"))
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.boot.starter.grpc.server)
    implementation(libs.caffeine)
}
