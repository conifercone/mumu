dependencies {
    implementation(project(":centaur-basis"))
    implementation(libs.spring.security.core)
    implementation(libs.spring.data.commons)
    annotationProcessor(project(":centaur-processor"))
}
