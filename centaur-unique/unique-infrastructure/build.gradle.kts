dependencies {
    implementation(project(":centaur-unique:unique-domain"))
    implementation(project(":centaur-unique:unique-client"))
    implementation(project(":centaur-extension"))
    implementation(libs.leaf) {
        exclude(group = "org.apache.zookeeper", module = "zookeeper")
    }
    implementation(libs.zookeeper)
    implementation(libs.redisOmSpring)
    annotationProcessor(libs.redisOmSpring)
}
