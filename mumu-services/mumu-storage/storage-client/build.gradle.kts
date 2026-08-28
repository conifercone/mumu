plugins {
    id("mumu.client-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-domain"))
    implementation(libs.spring.web)
    implementation(libs.spring.data.commons)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.boot.starter.grpc.server)
}
