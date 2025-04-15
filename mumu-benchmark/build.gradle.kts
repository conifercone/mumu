import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*

plugins {
    alias(libs.plugins.jmh)
}

dependencies {
    jmh(libs.bundles.jmh)
}

// 🔹 先获取 benchmark class name，供 jmh + saveBenchmarkResult 两处使用
val classNames: String = run {
    val primary = project.findProperty("include") as? String
    val fallback = project.findProperty("jmh.include") as? String
    val raw = primary ?: fallback
    if (raw.isNullOrBlank()) "AllBenchmarks"
    else raw.split('|').joinToString("+") { it.trim() }
}

val jmhFileName = "result_${classNames}.json"
val jmhReportFile = layout.buildDirectory.file("reports/jmh/$jmhFileName")
val jmhHistoryDir = layout.projectDirectory.dir("../benchmark-history")

// ✅ 设置 JMH 执行时的输出文件名
jmh {
    resultFormat.set("JSON")
    resultsFile.set(jmhReportFile)
}

// ✅ 保存历史的任务
tasks.register<Copy>("saveBenchmarkResult") {
    dependsOn("jmh")

    val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    sdf.timeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
    val timestamp = sdf.format(Date())

    val outputFileName = "result_${classNames}_$timestamp.json"

    from(jmhReportFile)
    into(jmhHistoryDir)
    rename { outputFileName }

    doLast {
        println("✅ Benchmark result saved to benchmark-history/$outputFileName")
    }
}
