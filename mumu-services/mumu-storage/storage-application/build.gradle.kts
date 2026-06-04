plugins {
    id("mumu.service-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-services:mumu-storage:storage-domain"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(libs.grpc.stub)
    implementation(libs.spring.grpc.server.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.security.core)
    implementation(libs.spring.web)
    implementation(libs.spring.tx)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.tika.core)
}
