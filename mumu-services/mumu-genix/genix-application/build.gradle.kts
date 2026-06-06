plugins {
    id("mumu.service-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-genix:genix-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.protobuf)
    implementation(libs.spring.boot.starter.actuator)
}
