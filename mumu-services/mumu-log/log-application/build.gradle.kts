dependencies {
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(project(":mumu-services:mumu-log:log-domain"))
    implementation(project(":mumu-services:mumu-log:log-infra"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.spring.grpc.server.spring.boot.starter)
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.data.elasticsearch)
}
