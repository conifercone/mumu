pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "mumu"
include("mumu-authentication")
include("mumu-authentication:authentication-adapter")
findProject(":mumu-authentication:authentication-adapter")?.name = "authentication-adapter"
include("mumu-authentication:authentication-application")
findProject(":mumu-authentication:authentication-application")?.name =
    "authentication-application"
include("mumu-authentication:authentication-domain")
findProject(":mumu-authentication:authentication-domain")?.name = "authentication-domain"
include("mumu-authentication:authentication-infrastructure")
findProject(":mumu-authentication:authentication-infrastructure")?.name =
    "authentication-infrastructure"
include("mumu-authentication:authentication-client")
findProject(":mumu-authentication:authentication-client")?.name = "authentication-client"
include("mumu-extension")
include("mumu-log")
include("mumu-log:log-adapter")
findProject(":mumu-log:log-adapter")?.name = "log-adapter"
include("mumu-log:log-application")
findProject(":mumu-log:log-application")?.name = "log-application"
include("mumu-log:log-client")
findProject(":mumu-log:log-client")?.name = "log-client"
include("mumu-log:log-domain")
findProject(":mumu-log:log-domain")?.name = "log-domain"
include("mumu-log:log-infrastructure")
findProject(":mumu-log:log-infrastructure")?.name = "log-infrastructure"
include("mumu-basis")
include("mumu-unique")
include("mumu-unique:unique-adapter")
findProject(":mumu-unique:unique-adapter")?.name = "unique-adapter"
include("mumu-unique:unique-client")
findProject(":mumu-unique:unique-client")?.name = "unique-client"
include("mumu-unique:unique-application")
findProject(":mumu-unique:unique-application")?.name = "unique-application"
include("mumu-unique:unique-domain")
findProject(":mumu-unique:unique-domain")?.name = "unique-domain"
include("mumu-unique:unique-infrastructure")
findProject(":mumu-unique:unique-infrastructure")?.name = "unique-infrastructure"
include("mumu-mail")
include("mumu-mail:mail-adapter")
findProject(":mumu-mail:mail-adapter")?.name = "mail-adapter"
include("mumu-mail:mail-application")
findProject(":mumu-mail:mail-application")?.name = "mail-application"
include("mumu-mail:mail-client")
findProject(":mumu-mail:mail-client")?.name = "mail-client"
include("mumu-mail:mail-domain")
findProject(":mumu-mail:mail-domain")?.name = "mail-domain"
include("mumu-mail:mail-infrastructure")
findProject(":mumu-mail:mail-infrastructure")?.name = "mail-infrastructure"
include("mumu-file")
include("mumu-file:file-adapter")
findProject(":mumu-file:file-adapter")?.name = "file-adapter"
include("mumu-file:file-application")
findProject(":mumu-file:file-application")?.name = "file-application"
include("mumu-file:file-client")
findProject(":mumu-file:file-client")?.name = "file-client"
include("mumu-file:file-domain")
findProject(":mumu-file:file-domain")?.name = "file-domain"
include("mumu-file:file-infrastructure")
findProject(":mumu-file:file-infrastructure")?.name = "file-infrastructure"
include("mumu-sms")
include("mumu-sms:sms-adapter")
findProject(":mumu-sms:sms-adapter")?.name = "sms-adapter"
include("mumu-sms:sms-application")
findProject(":mumu-sms:sms-application")?.name = "sms-application"
include("mumu-sms:sms-client")
findProject(":mumu-sms:sms-client")?.name = "sms-client"
include("mumu-sms:sms-domain")
findProject(":mumu-sms:sms-domain")?.name = "sms-domain"
include("mumu-sms:sms-infrastructure")
findProject(":mumu-sms:sms-infrastructure")?.name = "sms-infrastructure"
include("mumu-message")
include("mumu-message:message-adapter")
findProject(":mumu-message:message-adapter")?.name = "message-adapter"
include("mumu-message:message-application")
findProject(":mumu-message:message-application")?.name = "message-application"
include("mumu-message:message-domain")
findProject(":mumu-message:message-domain")?.name = "message-domain"
include("mumu-message:message-client")
findProject(":mumu-message:message-client")?.name = "message-client"
include("mumu-message:message-infrastructure")
findProject(":mumu-message:message-infrastructure")?.name = "message-infrastructure"
include("mumu-processor")
