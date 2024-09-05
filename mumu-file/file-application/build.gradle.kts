dependencies {
    implementation(project(":mumu-file:file-client"))
    implementation(project(":mumu-file:file-infrastructure"))
    implementation(project(":mumu-file:file-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.security.core)
}
