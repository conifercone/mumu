import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    id(libs.plugins.springboot.get().pluginId) version libs.versions.springbootVersion apply false
    id(libs.plugins.lombok.get().pluginId) version libs.versions.lombokPluginVersion
    id(libs.plugins.protobuf.get().pluginId) version libs.versions.protobufPluginVersion apply false
    id(libs.plugins.kotlinJvm.get().pluginId) version libs.versions.kotlinPluginVersion
    id(libs.plugins.kotlinPluginSpring.get().pluginId) version libs.versions.kotlinPluginVersion
    id(libs.plugins.kotlinPluginJpa.get().pluginId) version libs.versions.kotlinPluginVersion
    id(libs.plugins.signing.get().pluginId)
    id(libs.plugins.projectReport.get().pluginId)
    id(libs.plugins.checkstyle.get().pluginId)
    id(libs.plugins.pmd.get().pluginId)
}

@Suppress("UnstableApiUsage")
val gitHash = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.get().trim()

allprojects {

    group = findProperty("group")!! as String
    val versionString = findProperty("version")!! as String
    version =
        if (versionString.contains("-")) "$versionString-$gitHash" else versionString

    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(1, TimeUnit.HOURS)
            cacheDynamicVersionsFor(1, TimeUnit.HOURS)
        }

        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "ch.qos.logback", module = "logback-core")
    }

}

subprojects {
    apply(plugin = rootProject.libs.plugins.java.get().pluginId)
    apply(plugin = rootProject.libs.plugins.signing.get().pluginId)
    apply(plugin = rootProject.libs.plugins.projectReport.get().pluginId)
    apply(plugin = rootProject.libs.plugins.checkstyle.get().pluginId)
    apply(plugin = rootProject.libs.plugins.pmd.get().pluginId)
    apply(plugin = rootProject.libs.plugins.javaLibrary.get().pluginId)
    apply(plugin = rootProject.libs.plugins.idea.get().pluginId)
    apply(plugin = rootProject.libs.plugins.lombok.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlinJvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlinPluginSpring.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlinPluginJpa.get().pluginId)

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    checkstyle {
        toolVersion = "10.20.0"
    }

    pmd {
        isConsoleOutput = true
        toolVersion = "7.0.0"
        ruleSetFiles = files(
            rootProject.file("config/pmd/category/java/errorprone.xml"),
            rootProject.file("config/pmd/category/java/bestpractices.xml")
        )
    }

    signing {
        val mumuSigningKeyId = "mumu_signing_key_id"
        val mumuSigningKey = "mumu_signing_key"
        val mumuSigningPassword = "mumu_signing_password"
        if (!System.getenv(mumuSigningKeyId).isNullOrBlank() &&
            !System.getenv(mumuSigningKey).isNullOrBlank() &&
            !System.getenv(mumuSigningPassword).isNullOrBlank()
        ) {
            useInMemoryPgpKeys(
                System.getenv(mumuSigningKeyId) as String,
                file(System.getenv(mumuSigningKey) as String).readText(),
                System.getenv(mumuSigningPassword) as String
            )
            sign(tasks["jar"])
        }
    }

    tasks.register<Jar>("sourceJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    signing {
        sign(tasks.getByName("sourceJar"))
    }

    artifacts {
        add("archives", tasks.named("sourceJar"))
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
    }

    tasks.named<JavaCompile>("compileJava") {
        dependsOn(tasks.named("processResources"))
        doFirst {
            val hasProcessor = configurations["annotationProcessor"]
                .dependencies
                .any { it.name.contains("mumu-processor") }
            if (hasProcessor) {
                options.compilerArgs.addAll(
                    @Suppress("SpellCheckingInspection")
                    listOf(
                        "-Amapstruct.unmappedTargetPolicy=IGNORE",
                        "-Agradle.version=${gradle.gradleVersion}",
                        "-Aos.name=${System.getProperty("os.name")}",
                        "-Ajava.version=${System.getProperty("java.version")}",
                        "-Aproject.version=${project.version}",
                        "-Aproject.name=${project.name}",
                    )
                )
            } else {
                @Suppress("SpellCheckingInspection")
                options.compilerArgs.add("-Amapstruct.unmappedTargetPolicy=IGNORE")
            }
        }
    }

    tasks.named(
        "compileKotlin",
        org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java
    ) {
        compilerOptions {
            @Suppress("SpellCheckingInspection")
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.named<Jar>("jar") {
        into("META-INF/") {
            from(rootProject.file("LICENSE"))
        }
        manifest {
            attributes(
                "Implementation-Title" to archiveBaseName.get(),
                "Implementation-Version" to archiveVersion.get(),
                "Application-Version" to archiveVersion.get(),
                "Built-Gradle" to gradle.gradleVersion,
                "Build-OS" to System.getProperty("os.name"),
                "Build-Jdk" to System.getProperty("java.version"),
                "Build-Timestamp" to OffsetDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))
            )
        }
    }

    dependencies {
        implementation(platform(rootProject.libs.spring.boot.dependencies))
        annotationProcessor(platform(rootProject.libs.spring.boot.dependencies))
        implementation(platform(rootProject.libs.spring.cloud.dependencies))
        implementation(platform(rootProject.libs.grpc.bom))
        implementation(platform(rootProject.libs.protobuf.bom))
        implementation(platform(rootProject.libs.guava.bom))
        implementation(rootProject.libs.spring.boot.starter)
        implementation(rootProject.libs.spring.boot.starter.log4j2)
        implementation(rootProject.libs.spring.boot.starter.validation)
        implementation(rootProject.libs.disruptor)
        implementation(rootProject.libs.bundles.jackson)
        testImplementation(rootProject.libs.bundles.jackson)
        implementation(rootProject.libs.jetbrains.annotations)
        implementation(rootProject.libs.apiguardian.api)
        implementation(rootProject.libs.guava)
        implementation(rootProject.libs.commons.lang3)
        implementation(rootProject.libs.spring.boot.starter.undertow)
        implementation(rootProject.libs.commons.text)
        implementation(rootProject.libs.commons.io)
        implementation(rootProject.libs.commons.collections4)
        implementation(rootProject.libs.jackson.module.kotlin)
        implementation(rootProject.libs.kotlin.reflect)
        implementation(rootProject.libs.progressbar)
        implementation(rootProject.libs.swagger.annotations.jakarta)
        testImplementation(rootProject.libs.junit.jupiter)
        annotationProcessor(rootProject.libs.spring.boot.configuration.processor)
        implementation(rootProject.libs.mapstruct)
        annotationProcessor(rootProject.libs.mapstruct.processor)
        testAnnotationProcessor(rootProject.libs.mapstruct.processor)
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}
