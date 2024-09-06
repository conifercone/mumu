dependencies {
    implementation(project(":mumu-message:message-client"))
    implementation(project(":mumu-message:message-domain"))
    implementation(project(":mumu-message:message-infrastructure"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.tx)
}
