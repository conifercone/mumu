dependencies {
    implementation(project(":centaur-message:message-client"))
    implementation(project(":centaur-message:message-domain"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
