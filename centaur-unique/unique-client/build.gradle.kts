import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(project(":centaur-basis"))
    implementation(libs.consulDiscovery)
    implementation(libs.protobufJava)
    implementation(libs.grpcStub)
    implementation(libs.grpcProtobuf)
    implementation(libs.annotationApi)
    implementation(libs.yitterIdGenerator)
    implementation(libs.springBootActuator)
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }
    plugins {
        id("grpc") {
            artifact = libs.protocGenGrpcJava.get().toString()
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
