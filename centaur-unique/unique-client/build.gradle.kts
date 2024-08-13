import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(project(":centaur-basis"))
    annotationProcessor(project(":centaur-processor"))
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.javax.annotation.api)
    implementation(libs.yitter.idgenerator)
    implementation(libs.spring.boot.starter.actuator)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    plugins {
        id("grpc") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
            }
        }
    }
}
