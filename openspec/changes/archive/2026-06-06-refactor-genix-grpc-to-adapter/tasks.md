## 1. 依赖配置

- [x] 1.1 更新 genix-adapter/build.gradle.kts：添加 grpc-stub、spring-grpc-starter、mumu-extension
- [x] 1.2 更新 genix-application/build.gradle.kts：移除 grpc-stub、spring-grpc-starter，添加 grpc-protobuf

## 2. 创建 gRPC 适配器类

- [x] 2.1 创建 genix-adapter/grpc/CaptchaCodeGrpcService.java（3 个 gRPC 方法：generate / verify / delete）
- [x] 2.2 创建 genix-adapter/grpc/PrimaryKeyGrpcService.java（1 个 gRPC 方法：snowflake）

## 3. 修改 ServiceImpl

- [x] 3.1 CaptchaCodeServiceImpl：移除 @GrpcService、extends、gRPC 方法、gRPC 相关 import、CaptchaCodeAssemblerConvertor 注入
- [x] 3.2 PrimaryKeyServiceImpl：移除 @GrpcService、extends、gRPC 方法、gRPC 相关 import

## 4. 验证

- [x] 4.1 编译 genix-application 和 genix-adapter
