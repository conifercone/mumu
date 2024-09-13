dependencies {
    implementation(project(":mumu-basis"))
    implementation(project(":mumu-services:mumu-unique:unique-client"))
    implementation(project(":mumu-services:mumu-unique:unique-domain"))
    implementation(project(":mumu-services:mumu-unique:unique-infrastructure"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
}
