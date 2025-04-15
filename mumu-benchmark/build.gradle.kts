import java.text.SimpleDateFormat
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


tasks.register<Copy>("saveBenchmarkResult") {
    dependsOn("jmh")

    val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
    val outputFileName = "result_$timestamp.json"

    from(jmhReportFile)
    into(jmhHistoryDir)
    rename { outputFileName }

    doLast {
        println("✅ Benchmark result saved to benchmark-history/$outputFileName")
    }
}
