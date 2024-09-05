dependencies {
    implementation(project(":mumu-log:log-client"))
    implementation(project(":mumu-log:log-domain"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
