import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(project(":centaur-authentication:authentication-domain"))
    implementation(project(":centaur-extension"))
    implementation(libs.protobufJava)
    implementation(libs.grpcStub)
    implementation(libs.grpcProtobuf)
    implementation(libs.annotationApi)
    implementation(libs.consulDiscovery)
    implementation(libs.springBootActuator)
    implementation(libs.springDataCommons)
    implementation(libs.springWeb)
    implementation(libs.jakartaServletApi)
    implementation(libs.grpcSpringBootStarter)
    implementation(libs.grpcClientSpringBootStarter)
    api(libs.springBootOauth2ResourceServer)
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
