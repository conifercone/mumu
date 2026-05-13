plugins {
    id("mumu.java-conventions")
    id("mumu.kotlin-conventions")
    id("mumu.quality-conventions")
    id("mumu.publish-conventions")
    id("mumu.processor-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.spring.security.core)
    implementation(libs.spring.data.commons)
    annotationProcessor(project(":mumu-processor"))
}
