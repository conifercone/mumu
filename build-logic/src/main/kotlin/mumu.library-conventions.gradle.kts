plugins {
    id("mumu.base-conventions")
}

dependencies {
    implementation(libs.commons.text)
    implementation(libs.commons.io)
    
    implementation(libs.bundles.jackson)
    testImplementation(libs.bundles.jackson)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.moneta)
    
    implementation(libs.moneta)
    implementation(libs.progressbar)
    implementation(libs.jakarta.validation.api)
    
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    testAnnotationProcessor(libs.mapstruct.processor)
}
