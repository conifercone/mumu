dependencies {
    implementation(project(":centaur-message:message-client"))
    implementation(project(":centaur-message:message-domain"))
    implementation(project(":centaur-message:message-infrastructure"))
    implementation(project(":centaur-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.tx)
}
