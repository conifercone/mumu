dependencies {
    implementation(project(":centaur-file:file-domain"))
    implementation(project(":centaur-file:file-client"))
    implementation(project(":centaur-extension"))
    annotationProcessor(project(":centaur-processor"))
    implementation(libs.minio)
    implementation(libs.spring.web)
}
