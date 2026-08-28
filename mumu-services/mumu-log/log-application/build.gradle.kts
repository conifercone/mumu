plugins {
    id("mumu.application-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(project(":mumu-services:mumu-log:log-domain"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(libs.commons.lang3)
    implementation(libs.grpc.stub)
    implementation(libs.spring.boot.starter.grpc.server)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.data.elasticsearch)
}
