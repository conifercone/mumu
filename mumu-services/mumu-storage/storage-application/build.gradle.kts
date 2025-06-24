dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-services:mumu-storage:storage-infrastructure"))
    implementation(project(":mumu-services:mumu-storage:storage-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.grpc.stub)
    implementation(libs.grpc.server.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.security.core)
}
