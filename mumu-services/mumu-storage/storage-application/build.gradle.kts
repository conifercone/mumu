plugins {
    id("mumu.application-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-services:mumu-storage:storage-domain"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(libs.grpc.stub)
    implementation(libs.spring.boot.starter.grpc.server)
    implementation(libs.spring.security.core)
    implementation(libs.spring.web)
    implementation(libs.spring.tx)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.tika.core)
}
