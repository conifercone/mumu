dependencies {
    implementation(project(":mumu-services:mumu-unique:unique-application"))
    implementation(project(":mumu-services:mumu-unique:unique-client"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
}
