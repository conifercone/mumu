import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

subprojects {
    if (this.parent?.parent == rootProject) {
        apply(plugin = rootProject.libs.plugins.springboot.get().pluginId)

        tasks.withType<BootJar>().configureEach {
            into("META-INF/") {
                from(rootProject.file("LICENSE"))
            }
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
            finalizedBy("signBootJar")
        }

        signing {
            sign(tasks.getByName("bootJar"))
        }

        tasks.withType<BootBuildImage>().configureEach {
            imageName.set("mumu/${project.name}:${project.version}")
        }
    }

}
