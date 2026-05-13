plugins {
    java
}

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
