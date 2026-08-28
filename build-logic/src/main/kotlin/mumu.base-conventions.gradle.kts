import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * 所有模块的唯一底座：Java 25 + Lombok + Kotlin + 质量检查 + 发布 + 注解处理器参数。
 * 原 kotlin/quality/publish/processor 四个技术插件因全员使用（28/28 模块）下沉至此。
 */
plugins {
    `java-library`
    id("io.freefair.lombok")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.jpa")
    checkstyle
    pmd
    `project-report`
    idea
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}

dependencies {
    // 仅导入 BOM 用于版本管理，不引入具体依赖
    implementation(platform(libs.spring.boot.dependencies))
    annotationProcessor(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.grpc.bom))
    implementation(platform(libs.protobuf.bom))
    implementation(platform(libs.guava.bom))
    implementation(platform(libs.awssdk.bom))

    implementation(libs.jspecify)
    implementation(libs.apiguardian.api)
    implementation(libs.jakarta.annotation.api)
    implementation(libs.commons.lang3)
    implementation(libs.commons.collections4)
    implementation(libs.guava)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// ===== 质量检查（原 quality-conventions）=====

val checkstyleToolVersion = providers.gradleProperty("checkstyle.tool.version").get()
val pmdToolVersion = providers.gradleProperty("pmd.tool.version").get()

checkstyle {
    toolVersion = checkstyleToolVersion
}

val pmdConfigDir = rootProject.rootDir.resolve("config/pmd/category/java")
pmd {
    isConsoleOutput = true
    toolVersion = pmdToolVersion
    ruleSets = emptyList<String>()
}

tasks.withType<Pmd>().configureEach {
    incrementalAnalysis.set(true)
    outputs.cacheIf { true }
}

tasks.named("pmdMain", Pmd::class) {
    ruleSetFiles = files(
        pmdConfigDir.resolve("errorprone.xml"),
        pmdConfigDir.resolve("bestpractices.xml")
    )
}

tasks.named("pmdTest", Pmd::class) {
    ruleSetFiles = files(
        pmdConfigDir.resolve("errorprone_test.xml"),
        pmdConfigDir.resolve("bestpractices_test.xml")
    )
}

tasks.withType<Checkstyle>().configureEach {
    outputs.cacheIf { true }
}

// ===== 发布（原 publish-conventions）=====

tasks.register<Jar>("sourceJar") {
    description = "源代码任务"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.named<Jar>("jar") {
    dependsOn(tasks.named("sourceJar"))

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

// ===== 注解处理器参数（原 processor-conventions）=====

/**
 * 编译器参数提供者，用于支持 Gradle 配置缓存。
 * 通过使用 [CommandLineArgumentProvider] 和 Gradle 的 [Property] 系统，
 * 我们可以延迟计算参数值，同时确保所有输入都是可序列化的。
 */
interface MumuProcessorArgs : CommandLineArgumentProvider {
    @get:Input
    val hasProcessor: Property<Boolean>

    @get:Input
    val gradleVersion: Property<String>

    @get:Input
    val osName: Property<String>

    @get:Input
    val javaVersion: Property<String>

    @get:Input
    val projectVersion: Property<String>

    @get:Input
    val projectName: Property<String>

    override fun asArguments(): Iterable<String> {
        val args = mutableListOf("-Amapstruct.unmappedTargetPolicy=IGNORE")
        if (hasProcessor.getOrElse(false)) {
            args.addAll(
                listOf(
                    "-Agradle.version=${gradleVersion.get()}",
                    "-Aos.name=${osName.get()}",
                    "-Ajava.version=${javaVersion.get()}",
                    "-Aproject.version=${projectVersion.get()}",
                    "-Aproject.name=${projectName.get()}"
                )
            )
        }
        return args
    }
}

val projectVersionStr = project.version.toString()
val projectNameStr = project.name
val gradleVersionStr = gradle.gradleVersion
val osNameStr = System.getProperty("os.name") ?: "unknown"
val javaVersionStr = System.getProperty("java.version") ?: "unknown"

tasks.named<JavaCompile>("compileJava") {
    dependsOn(tasks.named("processResources"))

    // 创建并配置参数提供者
    val processorArgs = project.objects.newInstance<MumuProcessorArgs>().apply {
        hasProcessor.set(project.configurations.named("annotationProcessor").map { config ->
            config.allDependencies.any { it.name.contains("mumu-processor") }
        })
        gradleVersion.set(gradleVersionStr)
        osName.set(osNameStr)
        javaVersion.set(javaVersionStr)
        projectVersion.set(projectVersionStr)
        projectName.set(projectNameStr)
    }

    // 将参数提供者添加到 compileJava 任务中
    options.compilerArgumentProviders.add(processorArgs)
}
