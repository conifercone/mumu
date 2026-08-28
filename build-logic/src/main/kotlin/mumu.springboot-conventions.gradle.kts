import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * 服务 app 壳角色插件：Spring Boot 打包 + 4 个服务共有的 consul 注册/配置、API 文档、测试依赖。
 */
plugins {
    id("mumu.spring-conventions")
    id("org.springframework.boot")
}

dependencies {
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.consul.config)
    implementation(libs.bundles.web)
    implementation(libs.swagger3Ui)
    annotationProcessor(project(":mumu-processor"))

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
}

tasks.withType<BootJar>().configureEach {
    into("META-INF/") {
        from(rootProject.file("LICENSE"))
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Implementation-Title" to archiveBaseName.get(),
            "Implementation-Version" to archiveVersion.get(),
            "Application-Version" to archiveVersion.get(),
            "Built-Gradle" to gradle.gradleVersion,
            "Build-OS" to System.getProperty("os.name"),
            "Build-Jdk" to System.getProperty("java.version"),
            "Build-Timestamp" to OffsetDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }
}

tasks.withType<BootBuildImage>().configureEach {
    imageName.set("mumu/${project.name}:${project.version}")
}
