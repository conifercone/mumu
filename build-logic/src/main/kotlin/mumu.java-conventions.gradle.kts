plugins {
    id("mumu.library-conventions")
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.log4j2)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.disruptor)
    
    implementation(libs.kotlin.reflect)
    implementation(libs.swagger.annotations.jakarta)

    annotationProcessor(libs.spring.boot.configuration.processor)
}
