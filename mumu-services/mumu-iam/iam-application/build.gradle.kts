plugins {
    id("mumu.service-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(libs.grpc.protobuf)
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-iam:iam-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.spring.security.core)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.data.mongodb)
    implementation(libs.spring.tx)
}
