dependencies {
    implementation(project(":mumu-services:mumu-unique:unique-domain"))
    implementation(project(":mumu-services:mumu-unique:unique-client"))
    implementation(project(":mumu-extension"))
    implementation(libs.leaf) {
        exclude(group = "org.apache.zookeeper", module = "zookeeper")
    }
    implementation(libs.zookeeper)
    implementation(libs.redis.om.spring)
    implementation(libs.zxing.core)
    implementation(libs.zxing.javase)
    annotationProcessor(libs.redis.om.spring)
}
