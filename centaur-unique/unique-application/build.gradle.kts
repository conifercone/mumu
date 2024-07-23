dependencies {
    implementation(project(":centaur-basis"))
    implementation(project(":centaur-unique:unique-client"))
    implementation(project(":centaur-unique:unique-domain"))
    implementation(project(":centaur-unique:unique-infrastructure"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
}
