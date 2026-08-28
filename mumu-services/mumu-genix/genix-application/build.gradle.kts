plugins {
    id("mumu.application-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-genix:genix-domain"))
    implementation(libs.grpc.protobuf)
}
