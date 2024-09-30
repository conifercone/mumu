dependencies {
    implementation(project(":mumu-services:mumu-message:message-client"))
    implementation(project(":mumu-services:mumu-message:message-domain"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
