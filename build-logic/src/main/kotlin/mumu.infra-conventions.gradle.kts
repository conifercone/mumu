/**
 * infra 层角色插件：基础设施，存储技术依赖由各模块自行声明。
 */
plugins {
    id("mumu.spring-conventions")
}

dependencies {
    implementation(project(":mumu-extension"))
    annotationProcessor(project(":mumu-processor"))
}
