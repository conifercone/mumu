dependencies {
    implementation(project(":centaur-unique:unique-application"))
    implementation(project(":centaur-unique:unique-client"))
    implementation(project(":centaur-basis"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
}
