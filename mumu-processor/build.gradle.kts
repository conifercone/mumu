plugins {
    id("mumu.base-conventions")
    id("mumu.kotlin-conventions")
    id("mumu.quality-conventions")
    id("mumu.publish-conventions")
}

description = "Annotation processor"

dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.javapoet)
    implementation(libs.auto.service)
    annotationProcessor(libs.auto.service)
    implementation(libs.jakarta.persistence.api)
}
