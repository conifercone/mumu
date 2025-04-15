import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*

plugins {
    alias(libs.plugins.jmh)
}

dependencies {
    jmh(libs.bundles.jmh)
}

val jmhReportFile = layout.buildDirectory.file("reports/jmh/result.json")
val jmhHistoryDir = layout.projectDirectory.dir("../benchmark-history")

jmh {
    resultFormat.set("JSON")
    resultsFile.set(jmhReportFile)
}

fun getBenchmarkClassNames(): String {
    val primary = project.findProperty("include") as? String
    val fallback = project.findProperty("jmh.include") as? String
    val raw = primary ?: fallback
    return when {
        raw.isNullOrBlank() -> "AllBenchmarks"
        else -> raw.split('|').joinToString("+") { it.trim() }
    }
}

tasks.register<Copy>("saveBenchmarkResult") {
    dependsOn("jmh")

    val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    sdf.timeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
    val timestamp = sdf.format(Date())

    val classNames = getBenchmarkClassNames()
    val outputFileName = "result_${classNames}_$timestamp.json"

    from(jmhReportFile)
    into(jmhHistoryDir)
    rename { outputFileName }

    doLast {
        println("✅ Benchmark result saved to benchmark-history/$outputFileName")
    }
}
