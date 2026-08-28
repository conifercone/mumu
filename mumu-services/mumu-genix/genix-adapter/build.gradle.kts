plugins {
    id("mumu.adapter-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-genix:genix-application"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(libs.grpc.stub)
    implementation(libs.spring.boot.starter.grpc.server)
    implementation(project(":mumu-extension"))
}
