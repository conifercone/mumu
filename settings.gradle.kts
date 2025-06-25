pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
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
include("mumu-services:mumu-unique")
include("mumu-services:mumu-unique:unique-adapter")
findProject(":mumu-services:mumu-unique:unique-adapter")?.name = "unique-adapter"
include("mumu-services:mumu-unique:unique-client")
findProject(":mumu-services:mumu-unique:unique-client")?.name = "unique-client"
include("mumu-services:mumu-unique:unique-application")
findProject(":mumu-services:mumu-unique:unique-application")?.name = "unique-application"
include("mumu-services:mumu-unique:unique-domain")
findProject(":mumu-services:mumu-unique:unique-domain")?.name = "unique-domain"
include("mumu-services:mumu-unique:unique-infra")
findProject(":mumu-services:mumu-unique:unique-infra")?.name = "unique-infra"
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
