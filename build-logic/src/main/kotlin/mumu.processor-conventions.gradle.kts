plugins {
    java
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
