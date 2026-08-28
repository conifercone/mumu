plugins {
    id("mumu.infra-conventions")
}

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-domain"))
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(project(":mumu-services:mumu-genix:genix-client"))
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.protobuf)
    implementation(libs.spring.boot.starter.grpc.client)
    implementation(libs.spring.security.core)
    implementation(libs.spring.security.crypto)
    implementation(libs.jakarta.validation.api)
    implementation(libs.hypersistence)
    implementation(libs.postgresql)
    implementation(libs.redis.om.spring)
    implementation(libs.spring.boot.starter.data.mongodb)
    annotationProcessor(libs.redis.om.spring)
    annotationProcessor(libs.hibernate.processor)
    implementation(libs.jobrunr)
}
