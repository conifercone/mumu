plugins {
    id("mumu.java-conventions")
    id("mumu.kotlin-conventions")
    id("mumu.quality-conventions")
    id("mumu.publish-conventions")
    id("mumu.processor-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.swagger3Ui)
}
