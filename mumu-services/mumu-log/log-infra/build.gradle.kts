plugins {
    id("mumu.infra-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-log:log-domain"))
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.boot.starter.data.elasticsearch)
    implementation(libs.micrometer.tracing)
}
