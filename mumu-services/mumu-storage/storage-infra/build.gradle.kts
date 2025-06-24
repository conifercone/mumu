dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-domain"))
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-extension"))
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.minio)
    implementation(libs.spring.web)
}
