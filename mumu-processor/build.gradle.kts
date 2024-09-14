import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id(libs.plugins.mavenPublish.get().pluginId) version libs.versions.mavenPublishPluginVersion
}
dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.javapoet)
    implementation(libs.auto.service)
    annotationProcessor(libs.auto.service)
    implementation(libs.jakarta.persistence.api)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    coordinates(project.group.toString(), project.name, project.version.toString())

    pom {
        name.set(project.name)
        description.set("mumu project annotation processor")
        inceptionYear.set("2024")
        url.set("https://github.com/conifercone/mumu/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("conifercone")
                name.set("夷则")
                url.set("https://github.com/conifercone/")
            }
        }
        scm {
            url.set("https://github.com/conifercone/mumu/")
            connection.set("scm:git:git://github.com/conifercone/mumu.git")
            developerConnection.set("scm:git:ssh://git@github.com/conifercone/mumu.git")
        }
    }
    signAllPublications()
}
