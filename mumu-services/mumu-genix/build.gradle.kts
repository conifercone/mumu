plugins {
    id("mumu.springboot-conventions")
}

description = "Generation + Mix Service"

dependencies {
    implementation(project(":mumu-services:mumu-genix:genix-adapter"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-genix:genix-application"))
    implementation(project(":mumu-extension"))
    implementation(libs.cosid.spring.redis)
    implementation(libs.cosid.spring.boot.starter)
    implementation(libs.caffeine)
    testImplementation(libs.protobuf.java)
    testImplementation(libs.spring.boot.starter.grpc.client)
    implementation(libs.redis.om.spring)
    implementation(libs.spring.boot.starter.data.redis)
    annotationProcessor(libs.redis.om.spring)
}
