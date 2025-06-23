description = "Unique Service"

dependencies {
    implementation(project(":mumu-services:mumu-unique:unique-adapter"))
    implementation(project(":mumu-services:mumu-unique:unique-client"))
    implementation(project(":mumu-services:mumu-unique:unique-application"))
    implementation(project(":mumu-extension"))
    implementation(libs.bundles.micrometer)
    implementation(libs.swagger3Ui)
    implementation(libs.leaf)
    implementation(libs.zookeeper)
    implementation(libs.jasypt)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.caffeine)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.protobuf.java)
    testImplementation(libs.grpc.client.spring.boot.starter)
    implementation(libs.redis.om.spring)
    annotationProcessor(libs.redis.om.spring)
    annotationProcessor(project(":mumu-processor"))
}
