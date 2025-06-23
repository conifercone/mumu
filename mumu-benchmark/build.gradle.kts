import baby.mumu.build.enums.ModuleEnum

description = ModuleEnum.MUMU_BENCHMARK.description

dependencies {
    implementation(libs.jmh.core)
    annotationProcessor(libs.jmh.generator.annprocess)
    implementation(libs.yitter.idgenerator)
}
