import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.springboot) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.lombok)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSpring)
    alias(libs.plugins.kotlinPluginJpa)
    alias(libs.plugins.signing)
    alias(libs.plugins.projectReport)
    alias(libs.plugins.checkstyle)
    alias(libs.plugins.pmd)
}

val rootDirectory = project.rootDir
// 安装git hook
tasks.register<Copy>("installGitHooks") {
    group = "setup"
    description = "Copies git hooks to .git/hooks"
    // 源文件路径
    val hooksDir = file("${rootDirectory}/.git/hooks")
    val sourceDir = file("${rootDirectory}/scripts/git/hooks")
    val updateLicenseShell = file("${rootDirectory}/update_license.sh")
    // 将文件从源目录拷贝到目标目录
    from(sourceDir)
    // 目标目录
    into(hooksDir)
    // 设置执行权限（可选）
    doLast {
        // 设置 update_license.sh 的执行权限
        updateLicenseShell.setExecutable(true)
        // 设置 pre-commit 的执行权限
        hooksDir.resolve("pre-commit").setExecutable(true)
        // 设置 commit-msg 的执行权限
        hooksDir.resolve("commit-msg").setExecutable(true)
    }
}

val gitHash = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.get().trim()
val suffixes = listOf("-alpha", "-beta", "-snapshot", "-dev", "-test", "-pre")
val now: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)

@Suppress("SpellCheckingInspection")
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssXXX")
val formattedTime: String = now.format(formatter)
fun endsWithAny(input: String, suffixes: List<String>): Boolean {
    return suffixes.any { input.endsWith(it, ignoreCase = true) }
}

allprojects {

    group = findProperty("group")!! as String
    val versionString = findProperty("version")!! as String
    version =
        if (endsWithAny(
                versionString,
                suffixes
            )
        ) "$versionString-$gitHash-$formattedTime" else versionString

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
        exclude(group = "pull-parser", module = "pull-parser")
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

    tasks.withType<Pmd> {
        incrementalAnalysis = true
        outputs.cacheIf { true }
    }

    tasks.withType<Checkstyle> {
        outputs.cacheIf { true }
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

    val projectVersionStr = project.version.toString()
    val projectNameStr = project.name
    val gradleVersionStr = gradle.gradleVersion
    val osName = System.getProperty("os.name")
    val javaVersion = System.getProperty("java.version")

    val hasProcessor = providers.provider {
        configurations["annotationProcessor"]
            .dependencies
            .any { it.name.contains("mumu-processor") }
    }.get()

    tasks.named<JavaCompile>("compileJava") {
        dependsOn(tasks.named("processResources"))

        // 预先存储计算后的值
        inputs.property("projectVersion", projectVersionStr)
        inputs.property("projectName", projectNameStr)
        inputs.property("gradleVersion", gradleVersionStr)
        inputs.property("hasProcessor", hasProcessor)

        doFirst {
            val compilerArgs = mutableListOf("-Amapstruct.unmappedTargetPolicy=IGNORE")

            if (hasProcessor) {
                @Suppress("SpellCheckingInspection")
                compilerArgs.addAll(
                    listOf(
                        "-Agradle.version=$gradleVersionStr",
                        "-Aos.name=$osName",
                        "-Ajava.version=$javaVersion",
                        "-Aproject.version=$projectVersionStr",
                        "-Aproject.name=$projectNameStr"
                    )
                )
            }

            options.compilerArgs.addAll(compilerArgs)
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
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
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
        implementation(rootProject.libs.moneta)
        implementation(rootProject.libs.jackson.datatype.money)
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
