import baby.mumu.build.enums.ModuleEnum

description = ModuleEnum.MUMU_STORAGE.description

plugins {
    alias(libs.plugins.flyway)
}

dependencies {
    implementation(project(":mumu-services:mumu-iam:iam-client"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-storage:storage-infra"))
    implementation(project(":mumu-services:mumu-storage:storage-adapter"))
    implementation(project(":mumu-services:mumu-storage:storage-client"))
    implementation(project(":mumu-services:mumu-storage:storage-application"))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.bundles.micrometer)
    implementation(libs.grpc.server.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jasypt)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.grpc.client.spring.boot.starter)
    testImplementation(libs.spring.web)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.hypersistence.utils.hibernate63)
    implementation(libs.jasypt)
    implementation(libs.swagger3Ui)
    implementation(libs.minio)
    annotationProcessor(project(":mumu-processor"))
}

buildscript {
    dependencies {
        classpath(libs.flyway.database.postgresql)
    }
}

flyway {
    locations = arrayOf("classpath:db/migration/postgresql")
}
