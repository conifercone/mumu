dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.swagger3Ui)
}
