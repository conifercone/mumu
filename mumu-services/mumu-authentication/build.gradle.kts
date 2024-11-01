plugins {
    id(libs.plugins.flyway.get().pluginId) version libs.versions.flywayVersion
}

dependencies {
    implementation(project(":mumu-services:mumu-authentication:authentication-adapter"))
    implementation(project(":mumu-services:mumu-authentication:authentication-client"))
    implementation(project(":mumu-services:mumu-authentication:authentication-application"))
    implementation(project(":mumu-services:mumu-authentication:authentication-infrastructure"))
    implementation(project(":mumu-services:mumu-authentication:authentication-domain"))
    implementation(project(":mumu-extension"))
    implementation(project(":mumu-services:mumu-log:log-client"))
    implementation(libs.spring.boot.starter.oauth2.authorization.server)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.jasypt)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.swagger3Ui)
    implementation(libs.bundles.micrometer)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.spring.boot.starter.data.mongodb)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.skyscreamer", module = "jsonassert")
    }
    testImplementation(libs.jasypt)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.grpc.client.spring.boot.starter)
    testImplementation(project(":mumu-services:mumu-unique:unique-client"))
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.hypersistence.utils.hibernate63)
    implementation(libs.redis.om.spring)
    implementation(libs.jobrunr.spring.boot3.starter)
    annotationProcessor(libs.redis.om.spring)
    annotationProcessor(project(":mumu-processor"))
    implementation(libs.caffeine)
}

buildscript {
    dependencies {
        classpath(libs.flyway.database.postgresql)
    }
}

flyway {
    locations = arrayOf("classpath:db/migration/postgresql")
}
