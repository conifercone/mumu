dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-services:mumu-unique:unique-client"))
    implementation(project(":mumu-services:mumu-iam:iam-infra"))
    implementation(project(":mumu-services:mumu-iam:iam-domain"))
    implementation(project(":mumu-extension"))
    implementation(libs.spring.security.core)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.server.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.data.mongodb)
    implementation(libs.spring.tx)
}
