dependencies {
    implementation(project(":centaur-basis"))
    implementation(libs.javapoet)
    implementation(libs.auto.service)
    annotationProcessor(libs.auto.service)
    implementation(libs.jakarta.persistence.api)
}
