dependencies {
    implementation(project(":centaur-file:file-client"))
    implementation(project(":centaur-file:file-infrastructure"))
    implementation(project(":centaur-file:file-domain"))
    implementation(project(":centaur-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.security.core)
}
