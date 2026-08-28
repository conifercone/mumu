/**
 * application 层角色插件：应用服务与用例执行器。
 */
plugins {
    id("mumu.spring-conventions")
}

dependencies {
    implementation(project(":mumu-extension"))
    implementation(libs.spring.boot.starter.actuator)
}
