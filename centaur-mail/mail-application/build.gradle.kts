dependencies {
    implementation(project(":centaur-mail:mail-client"))
    implementation(project(":centaur-mail:mail-infrastructure"))
    implementation(project(":centaur-mail:mail-domain"))
    implementation(project(":centaur-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.security.core)
}
