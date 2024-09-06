dependencies {
    implementation(project(":mumu-basis"))
    implementation(project(":mumu-unique:unique-client"))
    implementation(project(":mumu-unique:unique-domain"))
    implementation(project(":mumu-unique:unique-infrastructure"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
}
