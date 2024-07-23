import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(project(":centaur-authentication:authentication-domain"))
    implementation(project(":centaur-extension"))
    implementation(libs.protobuf.java)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.javax.annotation.api)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.web)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.grpc.client.spring.boot.starter)
    api(libs.spring.boot.starter.oauth2.resource.server)
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
