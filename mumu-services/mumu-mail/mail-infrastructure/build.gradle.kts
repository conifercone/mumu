dependencies {
    implementation(project(":mumu-services:mumu-mail:mail-domain"))
    implementation(project(":mumu-services:mumu-mail:mail-client"))
    implementation(project(":mumu-services:mumu-file:file-client"))
    implementation(project(":mumu-extension"))
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.client.spring.boot.starter)
    implementation(libs.commons.io)
}
