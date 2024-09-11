import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.time.LocalDateTime
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
                    "Build-Timestamp" to LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
            }
        }

        signing {
            sign(tasks.getByName("bootJar"))
        }

        tasks.withType<BootBuildImage>().configureEach {
            imageName.set("mumu/${project.name}:${project.version}")
        }
    }

}
