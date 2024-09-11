dependencies {
    implementation(project(":mumu-services:mumu-file:file-domain"))
    implementation(project(":mumu-services:mumu-file:file-client"))
    implementation(project(":mumu-extension"))
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.minio)
    implementation(libs.spring.web)
}
