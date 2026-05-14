plugins {
    id("mumu.service-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.spring.security.core)
    implementation(libs.spring.data.commons)
    annotationProcessor(project(":mumu-processor"))
}
