import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    java
    `project-report`
    idea
}

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
