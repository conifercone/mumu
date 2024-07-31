import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
    id(libs.plugins.springboot.get().pluginId) version libs.versions.springbootVersion apply false
    id(libs.plugins.lombok.get().pluginId) version libs.versions.lombokPluginVersion
    id(libs.plugins.protobuf.get().pluginId) version libs.versions.protobufPluginVersion apply false
    id(libs.plugins.license.get().pluginId) version libs.versions.licensePluginVersion
    id(libs.plugins.kotlinJvm.get().pluginId) version libs.versions.kotlinPluginVersion
    id(libs.plugins.kotlinPluginSpring.get().pluginId) version libs.versions.kotlinPluginVersion
    id(libs.plugins.kotlinPluginJpa.get().pluginId) version libs.versions.kotlinPluginVersion
    id(libs.plugins.signing.get().pluginId)
}

allprojects {
    group = "com.sky"
    version = "1.0.3-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(0, "seconds")
            cacheDynamicVersionsFor(0, "seconds")
        }
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.springframework.boot:spring-boot-starter-tomcat:${rootProject.libs.versions.springbootVersion}}"))
                .using(module("org.springframework.boot:spring-boot-starter-undertow:${rootProject.libs.versions.springbootVersion}}"))
        }
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "ch.qos.logback", module = "logback-core")
    }

}

subprojects {
    apply(plugin = rootProject.libs.plugins.java.get().pluginId)
    apply(plugin = rootProject.libs.plugins.signing.get().pluginId)
    apply(plugin = rootProject.libs.plugins.javaLibrary.get().pluginId)
    apply(plugin = rootProject.libs.plugins.idea.get().pluginId)
    apply(plugin = rootProject.libs.plugins.lombok.get().pluginId)
    apply(plugin = rootProject.libs.plugins.license.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlinJvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlinPluginSpring.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlinPluginJpa.get().pluginId)

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    signing {
        useInMemoryPgpKeys(
            System.getenv("CENTAUR_SIGNING_KEY_ID") as String,
            file(System.getenv("CENTAUR_SIGNING_KEY_FILE") as String).readText(),
            System.getenv("CENTAUR_SIGNING_PASSWORD") as String
        )
        sign(configurations.runtimeElements.get())
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
    }

    tasks.named<JavaCompile>("compileJava") {
        dependsOn(tasks.named("processResources"))
        options.compilerArgs.add("-Amapstruct.unmappedTargetPolicy=IGNORE")
    }

    tasks.named(
        "compileKotlin",
        org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java
    ) {
        compilerOptions {
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
                "Built-Gradle" to gradle.gradleVersion,
                "Build-OS" to System.getProperty("os.name"),
                "Build-Jdk" to System.getProperty("java.version"),
                "Build-Timestamp" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "Automatic-Module-Name" to "${project.group}.${project.name.replace("-", ".")}"
            )
        }
    }

    license {
        encoding = StandardCharsets.UTF_8.name()
        ignoreFailures = true
        header = rootProject.file("SOURCE_CODE_HEAD.txt")
        val includes: MutableList<String> =
            mutableListOf("**/*.java", "**/*.kt", "**/*.xml", "**/*.yml")
        includes(includes)
        val excludes: MutableList<String> =
            mutableListOf("**/client/api/grpc/*.java", "**/*_.java", "**/*run.xml")
        excludes(excludes)
        mapping("java", "SLASHSTAR_STYLE")
        mapping("kt", "SLASHSTAR_STYLE")
        mapping("xml", "XML_STYLE")
        mapping("yml", "SCRIPT_STYLE")
        ext["year"] = Calendar.getInstance().get(Calendar.YEAR)
        ext["organization"] = "kaiyu.shan@outlook.com"
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
        implementation(rootProject.libs.jetbrains.annotations)
        implementation(rootProject.libs.apiguardian.api)
        implementation(rootProject.libs.guava)
        implementation(rootProject.libs.commons.lang3)
        implementation(rootProject.libs.commons.io)
        implementation(rootProject.libs.jackson.module.kotlin)
        implementation(rootProject.libs.kotlin.reflect)
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
