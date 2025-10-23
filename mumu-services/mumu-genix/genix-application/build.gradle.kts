dependencies {
    implementation(project(":mumu-basis"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-genix:genix-domain"))
    implementation(project(":mumu-services:mumu-genix:genix-infra"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.spring.grpc.server.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
}
