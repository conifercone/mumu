dependencies {
    implementation(project(":mumu-services:mumu-genix:genix-domain"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-extension"))
    implementation(libs.cosid.spring.boot.starter)
    implementation(libs.redis.om.spring)
    implementation(libs.zxing.core)
    implementation(libs.zxing.javase)
    annotationProcessor(libs.redis.om.spring)
}
