dependencies {
    implementation(project(":mumu-services:mumu-message:message-client"))
    implementation(project(":mumu-services:mumu-message:message-domain"))
    implementation(project(":mumu-services:mumu-message:message-infra"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.server.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.tx)
}
