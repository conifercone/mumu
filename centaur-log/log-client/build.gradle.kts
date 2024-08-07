import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.protobuf)
}


dependencies {
    implementation(project(":centaur-basis"))
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.javax.annotation.api)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
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

