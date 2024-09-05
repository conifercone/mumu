dependencies {
    implementation(project(":mumu-log:log-client"))
    implementation(project(":mumu-log:log-domain"))
    implementation(project(":mumu-log:log-infrastructure"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
}
