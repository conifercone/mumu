dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.spring.data.commons)
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.javax.annotation.api)
}
