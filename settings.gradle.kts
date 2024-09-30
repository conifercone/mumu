pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "mumu"
include("mumu-services:mumu-authentication")
include("mumu-services:mumu-authentication:authentication-adapter")
findProject(":mumu-services:mumu-authentication:authentication-adapter")?.name =
    "authentication-adapter"
include("mumu-services:mumu-authentication:authentication-application")
findProject(":mumu-services:mumu-authentication:authentication-application")?.name =
    "authentication-application"
include("mumu-services:mumu-authentication:authentication-domain")
findProject(":mumu-services:mumu-authentication:authentication-domain")?.name =
    "authentication-domain"
include("mumu-services:mumu-authentication:authentication-infrastructure")
findProject(":mumu-services:mumu-authentication:authentication-infrastructure")?.name =
    "authentication-infrastructure"
include("mumu-services:mumu-authentication:authentication-client")
findProject(":mumu-services:mumu-authentication:authentication-client")?.name =
    "authentication-client"
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
include("mumu-services:mumu-log:log-infrastructure")
findProject(":mumu-services:mumu-log:log-infrastructure")?.name = "log-infrastructure"
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
include("mumu-services:mumu-unique:unique-infrastructure")
findProject(":mumu-services:mumu-unique:unique-infrastructure")?.name = "unique-infrastructure"
include("mumu-services:mumu-mail")
include("mumu-services:mumu-mail:mail-adapter")
findProject(":mumu-services:mumu-mail:mail-adapter")?.name = "mail-adapter"
include("mumu-services:mumu-mail:mail-application")
findProject(":mumu-services:mumu-mail:mail-application")?.name = "mail-application"
include("mumu-services:mumu-mail:mail-client")
findProject(":mumu-services:mumu-mail:mail-client")?.name = "mail-client"
include("mumu-services:mumu-mail:mail-domain")
findProject(":mumu-services:mumu-mail:mail-domain")?.name = "mail-domain"
include("mumu-services:mumu-mail:mail-infrastructure")
findProject(":mumu-services:mumu-mail:mail-infrastructure")?.name = "mail-infrastructure"
include("mumu-services:mumu-file")
include("mumu-services:mumu-file:file-adapter")
findProject(":mumu-services:mumu-file:file-adapter")?.name = "file-adapter"
include("mumu-services:mumu-file:file-application")
findProject(":mumu-services:mumu-file:file-application")?.name = "file-application"
include("mumu-services:mumu-file:file-client")
findProject(":mumu-services:mumu-file:file-client")?.name = "file-client"
include("mumu-services:mumu-file:file-domain")
findProject(":mumu-services:mumu-file:file-domain")?.name = "file-domain"
include("mumu-services:mumu-file:file-infrastructure")
findProject(":mumu-services:mumu-file:file-infrastructure")?.name = "file-infrastructure"
include("mumu-services:mumu-sms")
include("mumu-services:mumu-sms:sms-adapter")
findProject(":mumu-services:mumu-sms:sms-adapter")?.name = "sms-adapter"
include("mumu-services:mumu-sms:sms-application")
findProject(":mumu-services:mumu-sms:sms-application")?.name = "sms-application"
include("mumu-services:mumu-sms:sms-client")
findProject(":mumu-services:mumu-sms:sms-client")?.name = "sms-client"
include("mumu-services:mumu-sms:sms-domain")
findProject(":mumu-services:mumu-sms:sms-domain")?.name = "sms-domain"
include("mumu-services:mumu-sms:sms-infrastructure")
findProject(":mumu-services:mumu-sms:sms-infrastructure")?.name = "sms-infrastructure"
include("mumu-services:mumu-message")
include("mumu-services:mumu-message:message-adapter")
findProject(":mumu-services:mumu-message:message-adapter")?.name = "message-adapter"
include("mumu-services:mumu-message:message-application")
findProject(":mumu-services:mumu-message:message-application")?.name = "message-application"
include("mumu-services:mumu-message:message-domain")
findProject(":mumu-services:mumu-message:message-domain")?.name = "message-domain"
include("mumu-services:mumu-message:message-client")
findProject(":mumu-services:mumu-message:message-client")?.name = "message-client"
include("mumu-services:mumu-message:message-infrastructure")
findProject(":mumu-services:mumu-message:message-infrastructure")?.name = "message-infrastructure"
include("mumu-processor")
include("mumu-services")
