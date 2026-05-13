import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    signing
    `project-report`
    idea
}

tasks.register<Jar>("sourceJar") {
    description = "源代码任务"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
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
    sign(tasks.getByName("jar"), tasks.getByName("sourceJar"))
}

tasks.withType<Jar>().configureEach {
    finalizedBy("signJar", "signSourceJar")
}

tasks.named("jar") {
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
