dependencies {
    implementation(libs.springSecurityCore)
    implementation(libs.jakartaServletApi)
    implementation(libs.springSecurityOauth2Core)
    implementation(libs.springSecurityOauth2Jose)
    implementation(libs.javaxMail)
    compileOnly(libs.springBootDataJpa)
    annotationProcessor(libs.hibernateJpamodelgen)
}
