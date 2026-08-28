plugins {
    id("mumu.client-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.spring.data.commons)
    implementation(libs.spring.web)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.boot.starter.grpc.server)
    api(libs.spring.boot.starter.security.oauth2.resource.server)
    implementation(libs.micrometer.tracing)
    implementation(libs.opencsv)
}
