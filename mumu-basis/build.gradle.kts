dependencies {
    implementation(libs.spring.security.core)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.security.oauth2.core)
    implementation(libs.spring.security.oauth2.jose)
    implementation(libs.javax.mail)
    api(libs.jakarta.persistence.api)
    compileOnly(libs.spring.boot.starter.data.jpa)
    annotationProcessor(libs.hibernate.jpamodelgen)
}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

signing {
    sign(tasks.getByName("sourceJar"))
}

artifacts {
    add("archives", tasks.named("sourceJar"))
}
