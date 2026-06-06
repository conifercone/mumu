plugins {
    id("mumu.service-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-genix:genix-application"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(libs.grpc.stub)
    implementation(libs.spring.grpc.server.spring.boot.starter)
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.swagger3Ui)
}
