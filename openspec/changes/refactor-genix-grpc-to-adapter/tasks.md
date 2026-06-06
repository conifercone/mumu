## 1. 依赖配置

- [ ] 1.1 更新 genix-adapter/build.gradle.kts：添加 grpc-stub、spring-grpc-starter、mumu-extension
- [ ] 1.2 更新 genix-application/build.gradle.kts：移除 grpc-stub、spring-grpc-starter，添加 grpc-protobuf

## 2. 创建 gRPC 适配器类

- [ ] 2.1 创建 genix-adapter/grpc/CaptchaCodeGrpcService.java（3 个 gRPC 方法：generate / verify / delete）
- [ ] 2.2 创建 genix-adapter/grpc/CountryGrpcService.java（2 个 gRPC 方法：findAll / findById）
- [ ] 2.3 创建 genix-adapter/grpc/PrimaryKeyGrpcService.java（1 个 gRPC 方法：snowflake）

## 3. 修改 ServiceImpl

- [ ] 3.1 CaptchaCodeServiceImpl：移除 @GrpcService、extends、gRPC 方法、gRPC 相关 import、CaptchaCodeAssemblerConvertor 注入
- [ ] 3.2 CountryServiceImpl：移除 @GrpcService、extends、gRPC 方法、gRPC 相关 import、CountryAssemblerConvertor 注入
- [ ] 3.3 PrimaryKeyServiceImpl：移除 @GrpcService、extends、gRPC 方法、gRPC 相关 import

## 4. 验证

- [ ] 4.1 编译 genix-application 和 genix-adapter
