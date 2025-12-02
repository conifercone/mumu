description = "Basic module"

dependencies {
    implementation(libs.spring.security.core)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.security.oauth2.core)
    implementation(libs.spring.security.oauth2.jose)
    api(libs.jakarta.persistence.api)
    compileOnly(libs.spring.boot.starter.data.jpa)
    compileOnly(libs.spring.boot.starter.data.mongodb)
    annotationProcessor(libs.hibernate.processor)
    implementation(libs.spring.cloud.commons)
    implementation(libs.grpc.api)
    implementation(libs.grpc.protobuf)
    implementation(libs.micrometer.tracing)
    implementation(libs.protobuf.java)
    implementation(libs.opencsv)
    implementation(libs.libphonenumber)
    implementation(libs.commons.validator)
}
