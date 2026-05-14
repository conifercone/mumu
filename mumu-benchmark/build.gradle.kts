plugins {
    id("mumu.base-conventions")
    id("mumu.kotlin-conventions")
    id("mumu.quality-conventions")
    id("mumu.publish-conventions")
}

description = "Benchmark module"

dependencies {
    implementation(libs.jmh.core)
    annotationProcessor(libs.jmh.generator.annprocess)
    implementation(libs.yitter.idgenerator)
}
