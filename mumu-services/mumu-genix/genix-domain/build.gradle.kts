plugins {
    id("mumu.service-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    annotationProcessor(project(":mumu-processor"))
}
