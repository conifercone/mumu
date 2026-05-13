import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

description = "The Delightfully Clean & Ready-to-Go Management System!"

val rootDirectory: File = project.rootDir

// 安装git hook
tasks.register<Copy>("installGitHooks") {
    group = "setup"
    description = "Copies git hooks to .git/hooks"
    // 源文件路径
    val hooksDir = file("${rootDirectory}/.git/hooks")
    val sourceDir = file("${rootDirectory}/scripts/git/hooks")
    // 将文件从源目录拷贝到目标目录
    from(sourceDir)
    // 目标目录
    into(hooksDir)
    // 设置执行权限（可选）
    doLast {
        // 设置 commit-msg 的执行权限
        hooksDir.resolve("commit-msg").setExecutable(true)
    }
}

// 获取git短hash用于版本号后缀
val gitHash = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.get().trim()
// 版本号后缀集合
val suffixes = listOf("alpha", "beta", "snapshot", "dev", "test", "pre")
// 当前UTC时间
val now: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
// 版本时间元数据格式
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssXXX")
val formattedTime: String = now.format(formatter)


val checkstyleToolVersion = findProperty("checkstyle.tool.version")!!.toString()
val pmdToolVersion = findProperty("pmd.tool.version")!!.toString()

allprojects {

    group = findProperty("group")!! as String
    val versionString = findProperty("version")!! as String
    val versionSuffixString = findProperty("version.suffix")!! as String
    // suffixes中包含的版本后缀追加gitHash，时间戳
    version =
        if (versionSuffixString.isNotBlank() && suffixes.contains(versionSuffixString)
        ) "$versionString-$versionSuffixString-$gitHash+$formattedTime" else if (versionSuffixString.isBlank()) versionString else "$versionString-$versionSuffixString"
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(0, TimeUnit.MINUTES)
            cacheDynamicVersionsFor(1, TimeUnit.HOURS)
        }
        listOf(
            "org.springframework.boot" to "spring-boot-starter-logging",
            "ch.qos.logback" to "logback-classic",
            "ch.qos.logback" to "logback-core",
            "pull-parser" to "pull-parser"
        ).forEach { (group, module) ->
            exclude(group = group, module = module)
        }
    }

}

// subprojects removed

