description = "Function expansion module"

dependencies {
    api(project(":mumu-basis"))
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.grpc.server.spring.boot.starter)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.security.core)
    implementation(libs.p6spy)
    compileOnly(libs.spring.boot.starter.data.jpa)
    implementation(libs.micrometer.tracing)
    implementation(libs.aliyun.mt)
    implementation(libs.aliyun.ocr)
    implementation(libs.asciitable)
    implementation(libs.deepl)
    implementation(libs.tess4j)
    implementation(libs.javacv)
    implementation(libs.bucket4j.lettuce)
    implementation(libs.lettuce.core)
    implementation(libs.spring.data.redis)
    testImplementation(libs.tess4j)
    testImplementation(libs.spring.test)
}
