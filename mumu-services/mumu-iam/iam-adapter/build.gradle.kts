plugins {
    id("mumu.adapter-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-services:mumu-iam:iam-application"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-iam:iam-domain"))
    implementation(libs.spring.security.core)
    implementation(libs.spring.data.commons)
    implementation(libs.grpc.stub)
    implementation(libs.spring.boot.starter.grpc.server)
}
