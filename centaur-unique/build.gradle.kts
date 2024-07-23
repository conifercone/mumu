apply(from = "../scripts/springboot.gradle")

dependencies {
    implementation(project(":centaur-unique:unique-adapter"))
    implementation(project(":centaur-unique:unique-client"))
    implementation(project(":centaur-unique:unique-application"))
    implementation(project(":centaur-extension"))
    implementation(libs.bundles.micrometer)
    implementation(libs.swagger3Ui)
    implementation(libs.leaf) {
        exclude(group = "org.apache.zookeeper", module = "zookeeper")
    }
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
}
