import java.io.ByteArrayOutputStream
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
    id(libs.plugins.projectReport.get().pluginId)
}

val gitHash: String by extra {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

allprojects {

    tasks.register("printGitHash") {
        doLast {
            println("Git Hash: $gitHash")
        }
    }

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
        val mumuSigningKeyId = "MUMU_SIGNING_KEY_ID"
        val mumuSigningKey = "MUMU_SIGNING_KEY"
        val mumuSigningPassword = "MUMU_SIGNING_PASSWORD"
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
                "Application-Version" to archiveVersion.get(),
                "Built-Gradle" to gradle.gradleVersion,
                "Build-OS" to System.getProperty("os.name"),
                "Build-Jdk" to System.getProperty("java.version"),
                "Build-Timestamp" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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
        ext["organization"] = "the original author or authors"
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
