description = "Generation + Mix Service"

dependencies {
    implementation(project(":mumu-services:mumu-genix:genix-adapter"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-genix:genix-application"))
    implementation(project(":mumu-extension"))
    implementation(libs.bundles.web)
    implementation(libs.swagger3Ui)
    implementation(libs.leaf)
    implementation(libs.zookeeper)
    implementation(libs.jasypt)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.caffeine)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.protobuf.java)
    testImplementation(libs.spring.grpc.client.spring.boot.starter)
    implementation(libs.redis.om.spring)
    implementation(libs.spring.boot.data.redis)
    annotationProcessor(libs.redis.om.spring)
    annotationProcessor(project(":mumu-processor"))
}
