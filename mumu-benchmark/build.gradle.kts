plugins {
    id("mumu.base-conventions")
}

description = "Benchmark module"

dependencies {
    implementation(libs.jmh.core)
    annotationProcessor(libs.jmh.generator.annprocess)
    implementation(libs.yitter.idgenerator)
}
