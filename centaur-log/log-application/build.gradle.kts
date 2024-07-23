dependencies {
    implementation(project(":centaur-log:log-client"))
    implementation(project(":centaur-log:log-domain"))
    implementation(project(":centaur-log:log-infrastructure"))
    implementation(project(":centaur-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
}
