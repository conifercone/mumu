description = "注解处理器"

dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.javapoet)
    implementation(libs.auto.service)
    annotationProcessor(libs.auto.service)
    implementation(libs.jakarta.persistence.api)
}
