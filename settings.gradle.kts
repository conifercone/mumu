pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.develocity") version ("4.2.2")
}

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
        uploadInBackground.set(true)
        publishing.onlyIf { false }
    }
}

buildCache {
    local {
        directory = file("${rootDir}/.gradle/build-cache")
    }
}

rootProject.name = "mumu"
include("mumu-services:mumu-iam")
include("mumu-services:mumu-iam:iam-adapter")
findProject(":mumu-services:mumu-iam:iam-adapter")?.name =
    "iam-adapter"
include("mumu-services:mumu-iam:iam-application")
findProject(":mumu-services:mumu-iam:iam-application")?.name =
    "iam-application"
include("mumu-services:mumu-iam:iam-domain")
findProject(":mumu-services:mumu-iam:iam-domain")?.name =
    "iam-domain"
include("mumu-services:mumu-iam:iam-infra")
findProject(":mumu-services:mumu-iam:iam-infra")?.name =
    "iam-infra"
include("mumu-services:mumu-iam:iam-client")
findProject(":mumu-services:mumu-iam:iam-client")?.name =
    "iam-client"
include("mumu-extension")
include("mumu-services:mumu-log")
include("mumu-services:mumu-log:log-adapter")
findProject(":mumu-services:mumu-log:log-adapter")?.name = "log-adapter"
include("mumu-services:mumu-log:log-application")
findProject(":mumu-services:mumu-log:log-application")?.name = "log-application"
include("mumu-services:mumu-log:log-client")
findProject(":mumu-services:mumu-log:log-client")?.name = "log-client"
include("mumu-services:mumu-log:log-domain")
findProject(":mumu-services:mumu-log:log-domain")?.name = "log-domain"
include("mumu-services:mumu-log:log-infra")
findProject(":mumu-services:mumu-log:log-infra")?.name = "log-infra"
include("mumu-basis")
include("mumu-services:mumu-genix")
include("mumu-services:mumu-genix:genix-adapter")
findProject(":mumu-services:mumu-genix:genix-adapter")?.name = "genix-adapter"
include("mumu-services:mumu-genix:genix-client")
findProject(":mumu-services:mumu-genix:genix-client")?.name = "genix-client"
include("mumu-services:mumu-genix:genix-application")
findProject(":mumu-services:mumu-genix:genix-application")?.name = "genix-application"
include("mumu-services:mumu-genix:genix-domain")
findProject(":mumu-services:mumu-genix:genix-domain")?.name = "genix-domain"
include("mumu-services:mumu-genix:genix-infra")
findProject(":mumu-services:mumu-genix:genix-infra")?.name = "genix-infra"
include("mumu-services:mumu-storage")
include("mumu-services:mumu-storage:storage-adapter")
findProject(":mumu-services:mumu-storage:storage-adapter")?.name = "storage-adapter"
include("mumu-services:mumu-storage:storage-application")
findProject(":mumu-services:mumu-storage:storage-application")?.name = "storage-application"
include("mumu-services:mumu-storage:storage-client")
findProject(":mumu-services:mumu-storage:storage-client")?.name = "storage-client"
include("mumu-services:mumu-storage:storage-domain")
findProject(":mumu-services:mumu-storage:storage-domain")?.name = "storage-domain"
include("mumu-services:mumu-storage:storage-infra")
findProject(":mumu-services:mumu-storage:storage-infra")?.name = "storage-infra"
include("mumu-processor")
include("mumu-services")
include("mumu-benchmark")
