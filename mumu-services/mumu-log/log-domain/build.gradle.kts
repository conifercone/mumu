plugins {
    id("mumu.service-conventions")
}

dependencies {
    implementation(libs.spring.data.commons)
    implementation(project(":mumu-basis"))
    annotationProcessor(project(":mumu-processor"))
}
