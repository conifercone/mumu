plugins {
    id("mumu.java-conventions")
    id("mumu.kotlin-conventions")
    id("mumu.quality-conventions")
    id("mumu.publish-conventions")
    id("mumu.processor-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-services:mumu-iam:iam-domain"))
    implementation(project(":mumu-basis"))
    implementation(libs.spring.security.core)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.swagger3Ui)
    implementation(libs.spring.data.commons)
}
