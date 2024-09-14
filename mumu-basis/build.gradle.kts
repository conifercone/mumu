import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id(libs.plugins.mavenPublish.get().pluginId) version libs.versions.mavenPublishPluginVersion
}

dependencies {
    implementation(libs.spring.security.core)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.security.oauth2.core)
    implementation(libs.spring.security.oauth2.jose)
    implementation(libs.javax.mail)
    api(libs.jakarta.persistence.api)
    compileOnly(libs.spring.boot.starter.data.jpa)
    annotationProcessor(libs.hibernate.jpamodelgen)
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    coordinates(project.group.toString(), project.name, project.version.toString())

    pom {
        name.set(project.name)
        description.set("mumu project basic dependencies")
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
