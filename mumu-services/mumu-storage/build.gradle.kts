plugins {
    id("mumu.springboot-conventions")
}

description = "Storage Management Service"

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-storage:storage-infra"))
    implementation(project(":mumu-services:mumu-storage:storage-adapter"))
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-services:mumu-storage:storage-application"))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.grpc.server)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.boot.starter.grpc.client)
    testImplementation(libs.spring.web)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.hypersistence)
    implementation(libs.minio)
    implementation(libs.s3)
}
