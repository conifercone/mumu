plugins {
    alias(libs.plugins.flyway)
}

dependencies {
    implementation(project(":mumu-services:mumu-authentication:authentication-client"))
    implementation(project(":mumu-services:mumu-message:message-infrastructure"))
    implementation(project(":mumu-services:mumu-message:message-adapter"))
    implementation(project(":mumu-services:mumu-message:message-client"))
    implementation(project(":mumu-services:mumu-message:message-application"))
    implementation(project(":mumu-extension"))
    implementation(libs.netty)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.bundles.micrometer)
    implementation(libs.grpc.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.skyscreamer", module = "jsonassert")
    }
    testImplementation(libs.jasypt)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.grpc.client.spring.boot.starter)
    testImplementation(libs.spring.web)
    implementation(libs.jasypt)
    implementation(libs.swagger3Ui)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.hypersistence.utils.hibernate63)
    implementation(libs.redis.om.spring)
    implementation(libs.jobrunr.spring.boot3.starter)
    annotationProcessor(libs.redis.om.spring)
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
