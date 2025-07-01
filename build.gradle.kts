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

description = "The Delightfully Clean & Ready-to-Go Management System!"

val rootDirectory = project.rootDir

// 安装git hook
tasks.register<Copy>("installGitHooks") {
    group = "setup"
    description = "Copies git hooks to .git/hooks"
    // 源文件路径
    val hooksDir = file("${rootDirectory}/.git/hooks")
    val sourceDir = file("${rootDirectory}/scripts/git/hooks")
    // 将文件从源目录拷贝到目标目录
    from(sourceDir)
    // 目标目录
    into(hooksDir)
    // 设置执行权限（可选）
    doLast {
        // 设置 commit-msg 的执行权限
        hooksDir.resolve("commit-msg").setExecutable(true)
    }
}

// 获取git短hash用于版本号后缀
val gitHash = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.get().trim()
// 版本号后缀集合
val suffixes = listOf("alpha", "beta", "snapshot", "dev", "test", "pre")
// 当前UTC时间
val now: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssXXX")
val formattedTime: String = now.format(formatter)


val javaMajorVersion = findProperty("java.major.version")!!.toString().toInt()
val checkstyleToolVersion = findProperty("checkstyle.tool.version")!!.toString()
val pmdToolVersion = findProperty("pmd.tool.version")!!.toString()

allprojects {

    group = findProperty("group")!! as String
    val versionString = findProperty("version")!! as String
    val versionSuffixString = findProperty("version.suffix")!! as String
    // suffixes中包含的版本后缀追加gitHash，时间戳
    version =
        if (versionSuffixString.isNotBlank() && suffixes.contains(versionSuffixString)
        ) "$versionString-$versionSuffixString-$gitHash-$formattedTime" else if (versionSuffixString.isBlank()) versionString else "$versionString-$versionSuffixString"
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(0, TimeUnit.MINUTES)
            cacheDynamicVersionsFor(1, TimeUnit.HOURS)
        }
        listOf(
            "org.springframework.boot" to "spring-boot-starter-logging",
            "org.springframework.boot" to "spring-boot-starter-tomcat",
            "ch.qos.logback" to "logback-classic",
            "ch.qos.logback" to "logback-core",
            "pull-parser" to "pull-parser"
        ).forEach { (group, module) ->
            exclude(group = group, module = module)
        }
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
            languageVersion.set(JavaLanguageVersion.of(javaMajorVersion))
        }
    }

    checkstyle {
        toolVersion = checkstyleToolVersion
    }

    val pmdConfigDir = rootDir.resolve("config/pmd/category/java")
    pmd {
        isConsoleOutput = true
        toolVersion = pmdToolVersion
        ruleSetFiles = files(
            pmdConfigDir.resolve("errorprone.xml"),
            pmdConfigDir.resolve("bestpractices.xml")
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
        val mumuSigningKeyId = "MUMU_SIGNING_KEY_ID"
        val mumuSigningKeyFilePath = "MUMU_SIGNING_KEY_FILE_PATH"
        val mumuSigningKeyContent = "MUMU_SIGNING_KEY_CONTENT"
        val mumuSigningPassword = "MUMU_SIGNING_PASSWORD"

        val keyId = System.getenv(mumuSigningKeyId)
        val keyFile = System.getenv(mumuSigningKeyFilePath)
        val password = System.getenv(mumuSigningPassword)
        val keyContent = System.getenv(mumuSigningKeyContent)

        if (keyId.isNullOrBlank()) {
            throw GradleException("Environment variable '$mumuSigningKeyId' is not set.")
        }
        if (keyFile.isNullOrBlank() && keyContent.isNullOrBlank()) {
            throw GradleException("Environment variable '$mumuSigningKeyFilePath' or '$mumuSigningKeyContent' is not set.")
        }
        if (password.isNullOrBlank()) {
            throw GradleException("Environment variable '$mumuSigningPassword' is not set.")
        }

        useInMemoryPgpKeys(
            keyId,
            if (keyContent.isNullOrBlank()) file(keyFile).readText() else keyContent,
            password
        )
        sign(tasks["jar"])
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

    val hasProcessorProvider = providers.provider {
        configurations["annotationProcessor"].dependencies
            .any { it.name.contains("mumu-processor") }
    }
    tasks.named<JavaCompile>("compileJava") {
        dependsOn(tasks.named("processResources"))

        // 预先存储计算后的值
        inputs.property("projectVersion", projectVersionStr)
        inputs.property("projectName", projectNameStr)
        inputs.property("gradleVersion", gradleVersionStr)

        doFirst {
            val compilerArgs = mutableListOf("-Amapstruct.unmappedTargetPolicy=IGNORE")
            val hasProcessor = hasProcessorProvider.get()
            if (hasProcessor) {
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
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)
        annotationProcessor(rootProject.libs.spring.boot.configuration.processor)
        implementation(rootProject.libs.mapstruct)
        annotationProcessor(rootProject.libs.mapstruct.processor)
        testAnnotationProcessor(rootProject.libs.mapstruct.processor)
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}
