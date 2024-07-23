dependencies {
    implementation(libs.spring.security.core)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.security.oauth2.core)
    implementation(libs.spring.security.oauth2.jose)
    implementation(libs.javax.mail)
    compileOnly(libs.spring.boot.starter.data.jpa)
    annotationProcessor(libs.hibernate.jpamodelgen)
}
