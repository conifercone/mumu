plugins {
    id("mumu.service-conventions")
    id("mumu.protobuf-conventions")
}


dependencies {
    implementation(project(":mumu-services:mumu-storage:storage-domain"))
    implementation(project(":mumu-basis"))
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.spring.web)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.javax.annotation.api)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.resilience4j.retry)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.grpc.server.spring.boot.starter)
    implementation(libs.spring.grpc.client.spring.boot.starter)
    api(libs.protobuf.java)
}
