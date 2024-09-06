dependencies {
    implementation(project(":mumu-message:message-client"))
    implementation(project(":mumu-message:message-domain"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
