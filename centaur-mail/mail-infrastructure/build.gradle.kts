dependencies {
    implementation(project(":centaur-mail:mail-domain"))
    implementation(project(":centaur-mail:mail-client"))
    implementation(project(":centaur-file:file-client"))
    implementation(project(":centaur-extension"))
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.client.spring.boot.starter)
    implementation(libs.commons.io)
}
