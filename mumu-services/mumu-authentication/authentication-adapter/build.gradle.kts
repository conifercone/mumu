dependencies {
    implementation(project(":mumu-services:mumu-authentication:authentication-client"))
    implementation(project(":mumu-services:mumu-authentication:authentication-domain"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.security.core)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
