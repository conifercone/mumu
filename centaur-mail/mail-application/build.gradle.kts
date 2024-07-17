dependencies {
    implementation(project(":centaur-mail:mail-client"))
    implementation(project(":centaur-mail:mail-infrastructure"))
    implementation(project(":centaur-mail:mail-domain"))
    implementation(project(":centaur-extension"))
    implementation(libs.grpcStub)
    implementation(libs.grpcSpringBootStarter)
    implementation(libs.springBootActuator)
    implementation(libs.springSecurityCore)
}
