plugins {
    checkstyle
    pmd
}

val checkstyleToolVersion = providers.gradleProperty("checkstyle.tool.version").get()
val pmdToolVersion = providers.gradleProperty("pmd.tool.version").get()

checkstyle {
    toolVersion = checkstyleToolVersion
}

val pmdConfigDir = rootProject.rootDir.resolve("config/pmd/category/java")
pmd {
    isConsoleOutput = true
    toolVersion = pmdToolVersion
    ruleSets = emptyList<String>()
}

tasks.withType<Pmd>().configureEach {
    incrementalAnalysis.set(true)
    outputs.cacheIf { true }
}

tasks.named("pmdMain", Pmd::class) {
    ruleSetFiles = files(
        pmdConfigDir.resolve("errorprone.xml"),
        pmdConfigDir.resolve("bestpractices.xml")
    )
}

tasks.named("pmdTest", Pmd::class) {
    ruleSetFiles = files(
        pmdConfigDir.resolve("errorprone_test.xml"),
        pmdConfigDir.resolve("bestpractices_test.xml")
    )
}

tasks.withType<Checkstyle>().configureEach {
    outputs.cacheIf { true }
}
