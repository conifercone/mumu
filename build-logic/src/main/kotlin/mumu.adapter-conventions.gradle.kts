/**
 * adapter 层角色插件：入站适配器，REST 接口 + API 文档。
 */
plugins {
    id("mumu.spring-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.swagger3Ui)
}
