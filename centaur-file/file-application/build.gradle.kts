dependencies {
    implementation(project(":centaur-file:file-client"))
    implementation(project(":centaur-file:file-infrastructure"))
    implementation(project(":centaur-file:file-domain"))
    implementation(project(":centaur-extension"))
    implementation(libs.grpcStub)
    implementation(libs.grpcSpringBootStarter)
    implementation(libs.springBootActuator)
    implementation(libs.springSecurityCore)
}
