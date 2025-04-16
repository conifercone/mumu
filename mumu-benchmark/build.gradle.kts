import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*

plugins {
    alias(libs.plugins.jmh)
}

dependencies {
    jmh(libs.bundles.jmh)
}

val classNames: String = run {
    val primary = project.findProperty("include") as? String
    val fallback = project.findProperty("jmh.include") as? String
    val raw = primary ?: fallback
    if (raw.isNullOrBlank()) "AllBenchmarks"
    else raw.split('|').joinToString("+") { it.trim() }
}

val jmhFileName = "result_${classNames}.json"

val jmhHistoryDir = layout.projectDirectory.dir("../benchmark-history")
val jmhHistorySubDir = jmhHistoryDir.dir(classNames)

jmh {
    resultFormat.set("JSON")
    resultsFile.set(layout.buildDirectory.file("reports/jmh/$jmhFileName"))
}

tasks.register<Copy>("saveBenchmarkResult") {
    dependsOn("jmh")

    val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    sdf.timeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
    val timestamp = sdf.format(Date())

    val outputFileName = "result_${classNames}_$timestamp.json"
    val jmhReportFile = layout.buildDirectory.file("reports/jmh/$jmhFileName")

    from(jmhReportFile)
    into(jmhHistorySubDir) // 复制到具体的 classNames 子目录里
    rename { outputFileName }

    doLast {
        println("✅ Benchmark result saved to ${jmhHistorySubDir.asFile}/$outputFileName")

        // 🔹 保留 7 天历史数据的清理逻辑
        val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val oldFiles = jmhHistorySubDir.asFile.listFiles()?.filter { file ->
            file.isFile && file.lastModified() < sevenDaysAgo
        } ?: emptyList()

        oldFiles.forEach { file ->
            if (file.delete()) {
                println("🗑️ Deleted old benchmark file: ${file.name}")
            }
        }
    }
}
