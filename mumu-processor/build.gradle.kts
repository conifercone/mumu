import baby.mumu.build.enums.ModuleEnum

description = ModuleEnum.MUMU_PROCESSOR.description

dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.javapoet)
    implementation(libs.auto.service)
    annotationProcessor(libs.auto.service)
    implementation(libs.jakarta.persistence.api)
}
