plugins {
    id("mumu.adapter-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(project(":mumu-services:mumu-log:log-domain"))
    implementation(libs.spring.data.commons)
}
