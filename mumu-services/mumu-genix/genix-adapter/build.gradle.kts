dependencies {
    implementation(project(":mumu-services:mumu-genix:genix-application"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
}
