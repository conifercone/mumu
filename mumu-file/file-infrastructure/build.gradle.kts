dependencies {
    implementation(project(":mumu-file:file-domain"))
    implementation(project(":mumu-file:file-client"))
    implementation(project(":mumu-extension"))
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.minio)
    implementation(libs.spring.web)
}
