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
    implementation(libs.consulDiscovery)
    implementation(libs.consulConfig)
    implementation(libs.caffeine)
    testImplementation(libs.springbootTest)
    testImplementation(libs.protobufJava)
    testImplementation(libs.grpcClientSpringBootStarter)
    implementation(libs.redisOmSpring)
    annotationProcessor(libs.redisOmSpring)
}
