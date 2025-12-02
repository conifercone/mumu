dependencies {
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(project(":mumu-services:mumu-log:log-domain"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
