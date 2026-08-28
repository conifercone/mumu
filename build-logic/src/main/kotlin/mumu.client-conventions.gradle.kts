/**
 * client 层角色插件：API 契约与 gRPC 客户端，含 protobuf 生成。
 */
plugins {
    id("mumu.spring-conventions")
    id("mumu.protobuf-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    annotationProcessor(project(":mumu-processor"))

    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.boot.starter.grpc.client)
    api(libs.protobuf.java)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.javax.annotation.api)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.resilience4j.retry)
}
