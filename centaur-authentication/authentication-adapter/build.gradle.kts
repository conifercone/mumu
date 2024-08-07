dependencies {
    implementation(project(":centaur-authentication:authentication-client"))
    implementation(project(":centaur-authentication:authentication-domain"))
    implementation(project(":centaur-basis"))
    implementation(libs.spring.security.core)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
