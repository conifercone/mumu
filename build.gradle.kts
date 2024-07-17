import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
    id(libs.plugins.dependencyManagement.get().pluginId) version (libs.plugins.dependencyManagement.get().version.toString())
    id(libs.plugins.springboot.get().pluginId) version (libs.plugins.springboot.get().version.toString()) apply false
    id(libs.plugins.lombok.get().pluginId) version (libs.plugins.lombok.get().version.toString())
    id(libs.plugins.protobuf.get().pluginId) version (libs.plugins.protobuf.get().version.toString()) apply false
    id(libs.plugins.license.get().pluginId) version (libs.plugins.license.get().version.toString())
    id(libs.plugins.kotlinJvm.get().pluginId) version (libs.plugins.kotlinJvm.get().version.toString())
    id(libs.plugins.kotlinPluginSpring.get().pluginId) version (libs.plugins.kotlinPluginSpring.get().version.toString())
    id(libs.plugins.kotlinPluginJpa.get().pluginId) version (libs.plugins.kotlinPluginJpa.get().version.toString())
}

allprojects {
    group = "com.sky"
    version = "1.0.2-SNAPSHOT"

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
    }

}

subprojects {
    apply(plugin = rootProject.libs.plugins.java.get().pluginId)
    apply(plugin = rootProject.libs.plugins.javaLibrary.get().pluginId)
    apply(plugin = rootProject.libs.plugins.idea.get().pluginId)
    apply(plugin = rootProject.libs.plugins.dependencyManagement.get().pluginId)
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

    dependencyManagement {
        imports {
            mavenBom(rootProject.libs.springBootDependencies.get().toString())
            mavenBom(rootProject.libs.springCloudDependencies.get().toString())
            mavenBom(rootProject.libs.grpcBom.get().toString())
            mavenBom(rootProject.libs.protobufBom.get().toString())
            mavenBom(rootProject.libs.guavaBom.get().toString())
        }
    }

    dependencies {
        implementation(rootProject.libs.springboot)
        implementation(rootProject.libs.springBootLog4j2)
        implementation(rootProject.libs.springBootValidation)
        implementation(rootProject.libs.disruptor)
        implementation(rootProject.libs.bundles.jackson)
        implementation(rootProject.libs.jetbrainsAnnotations)
        implementation(rootProject.libs.apiguardian)
        implementation(rootProject.libs.guava)
        implementation(rootProject.libs.commonsLang3)
        implementation(rootProject.libs.commonsIo)
        implementation(rootProject.libs.jacksonModuleKotlin)
        implementation(rootProject.libs.kotlinReflect)
        testImplementation(rootProject.libs.junitJupiter)
        annotationProcessor(rootProject.libs.springBootConfigurationProcessor)
        implementation(rootProject.libs.mapstruct)
        annotationProcessor(rootProject.libs.mapstructProcessor)
        testAnnotationProcessor(rootProject.libs.mapstructProcessor)
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}
