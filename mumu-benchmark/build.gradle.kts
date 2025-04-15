plugins {
    alias(libs.plugins.jmh)
}

dependencies {
    jmh(libs.bundles.jmh)
}

jmh {
    resultFormat.set("JSON")
    resultsFile.set(layout.buildDirectory.file("reports/jmh/result.json"))
}
