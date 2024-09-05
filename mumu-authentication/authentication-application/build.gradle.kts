dependencies {
    implementation(project(":mumu-authentication:authentication-client"))
    implementation(project(":mumu-unique:unique-client"))
    implementation(project(":mumu-authentication:authentication-infrastructure"))
    implementation(project(":mumu-authentication:authentication-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.spring.security.core)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.tx)
}
