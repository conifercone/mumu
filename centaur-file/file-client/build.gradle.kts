import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.protobuf)
}


dependencies {
    implementation(project(":centaur-basis"))
    implementation(libs.springWeb)
    implementation(libs.consulDiscovery)
    implementation(libs.protobufJava)
    implementation(libs.grpcStub)
    implementation(libs.grpcProtobuf)
    implementation(libs.annotationApi)
    implementation(libs.springBootActuator)
    implementation(libs.springDataCommons)
    implementation(libs.grpcSpringBootStarter)
    implementation(libs.grpcClientSpringBootStarter)
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
