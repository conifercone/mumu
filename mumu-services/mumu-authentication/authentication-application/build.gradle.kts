dependencies {
    implementation(project(":mumu-services:mumu-authentication:authentication-client"))
    implementation(project(":mumu-services:mumu-unique:unique-client"))
    implementation(project(":mumu-services:mumu-authentication:authentication-infrastructure"))
    implementation(project(":mumu-services:mumu-authentication:authentication-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.spring.security.core)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.server.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.data.mongodb)
    implementation(libs.spring.tx)
}
