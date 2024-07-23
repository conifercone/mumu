dependencies {
    implementation(project(":centaur-authentication:authentication-client"))
    implementation(project(":centaur-unique:unique-client"))
    implementation(project(":centaur-authentication:authentication-infrastructure"))
    implementation(project(":centaur-authentication:authentication-domain"))
    implementation(project(":centaur-extension"))
    implementation(libs.spring.security.core)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.tx)
}
