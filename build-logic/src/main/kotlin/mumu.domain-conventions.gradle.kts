/**
 * domain 层角色插件：领域核心，依赖基础模块并启用元模型生成。
 */
plugins {
    id("mumu.spring-conventions")
}

dependencies {
    implementation(project(":mumu-basis"))
    annotationProcessor(project(":mumu-processor"))
}
