import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

// 注意：LibrariesForLibs 是 Gradle 自动生成的类。
// 我们在 build-logic/build.gradle.kts 中通过 implementation(files(...)) 将其加入类路径。
// 这样我们就可以在规约插件中使用类型安全的 libs 访问器了。
val Project.libs: org.gradle.accessors.dm.LibrariesForLibs
    get() = the<org.gradle.accessors.dm.LibrariesForLibs>()
