apply(from = "../scripts/springboot.gradle")

dependencies {
    implementation(project(":centaur-extension"))
    implementation(libs.consulDiscovery)
    implementation(libs.consulConfig)
    implementation(libs.bundles.micrometer)
    implementation(libs.grpcSpringBootStarter)
    testImplementation(libs.springbootTest)
    testImplementation(libs.jasypt)
    testImplementation(libs.springSecurityTest)
    testImplementation(libs.grpcClientSpringBootStarter)
    testImplementation(libs.springWeb)
    implementation(libs.jasypt)
    implementation(libs.swagger3Ui)
}
