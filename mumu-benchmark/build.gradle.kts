plugins {
    alias(libs.plugins.jmh)
}

dependencies {
    jmh(libs.bundles.jmh)
    jmh(libs.yitter.idgenerator)
}

val classNames: String = run {
    val raw = project.findProperty("jmh.include") as? String
    if (raw.isNullOrBlank()) "AllBenchmarks"
    else raw.split('|').joinToString("+") { it.trim() }
}

val jmhFileName = "result_${classNames}.json"

jmh {
    resultFormat.set("JSON")
    resultsFile.set(layout.buildDirectory.file("reports/jmh/$jmhFileName"))
}
