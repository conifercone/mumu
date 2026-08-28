plugins {
    id("mumu.application-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(libs.grpc.protobuf)
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-iam:iam-domain"))
    implementation(libs.spring.security.core)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.data.mongodb)
    implementation(libs.spring.tx)
}
