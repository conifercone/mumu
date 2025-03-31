dependencies {
    implementation(project(":mumu-services:mumu-mail:mail-client"))
    implementation(project(":mumu-services:mumu-mail:mail-infrastructure"))
    implementation(project(":mumu-services:mumu-mail:mail-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.server.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.security.core)
}
