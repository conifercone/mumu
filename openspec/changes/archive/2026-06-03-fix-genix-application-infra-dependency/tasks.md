## 1. CaptchaCode 拆分

- [x] 1.1 在 `genix-infra/captcha/mapper/` 创建 `CaptchaCodePersistenceMapper.java`（仅含 `toCaptchaCodeCacheablePO` 方法，从原 `CaptchaCodeMapper` 提取 Entity→CachePO 映射）
- [x] 1.2 在 `genix-infra/captcha/convertor/` 创建 `CaptchaCodePersistenceConvertor.java`（包装 `CaptchaCodePersistenceMapper`，提供 `toCaptchaCodeCacheablePO`）
- [x] 1.3 更新 `CaptchaCodeGatewayImpl`，注入 `CaptchaCodePersistenceConvertor` 替换旧的 `CaptchaCodeConvertor`
- [x] 1.4 在 `genix-application/captcha/convertor/` 创建 `CaptchaCodeAssemblerMapper.java`（从原 `CaptchaCodeMapper` 提取其余 5 个方法：Cmd→Entity ×2、Entity→DTO、gRPC→Cmd、DTO→gRPCDTO）
- [x] 1.5 在 `genix-application/captcha/convertor/` 创建 `CaptchaCodeAssemblerConvertor.java`（包装 `CaptchaCodeAssemblerMapper`，提供 5 个方法）
- [x] 1.6 删除旧的 `genix-infra/captcha/convertor/CaptchaCodeConvertor.java` 和 `CaptchaCodeMapper.java`

## 2. Country 移动

- [x] 2.1 在 `genix-application/country/convertor/` 创建 `CountryAssemblerMapper.java`（内容同原 `CountryMapper`）
- [x] 2.2 在 `genix-application/country/convertor/` 创建 `CountryAssemblerConvertor.java`（内容同原 `CountryConvertor`，引用 `CountryAssemblerMapper.INSTANCE`）
- [x] 2.3 更新 7 个 country executor 的 import，从 `genix.infra.country.convertor.CountryConvertor` 改为 `genix.application.country.convertor.CountryAssemblerConvertor`
- [x] 2.4 删除旧的 `genix-infra/country/convertor/CountryConvertor.java` 和 `CountryMapper.java`

## 3. BarCode 移动

- [x] 3.1 在 `genix-application/barcode/convertor/` 创建 `BarCodeAssemblerMapper.java` 和 `BarCodeAssemblerConvertor.java`
- [x] 3.2 更新 `BarCodeGenerateCmdExe` 的 import
- [x] 3.3 删除旧的 `genix-infra/barcode/convertor/BarCodeConvertor.java` 和 `BarCodeMapper.java`

## 4. QRCode 移动

- [x] 4.1 在 `genix-application/qrcode/convertor/` 创建 `QRCodeAssemblerMapper.java` 和 `QRCodeAssemblerConvertor.java`
- [x] 4.2 更新 `QRCodeGenerateCmdExe` 的 import
- [x] 4.3 删除旧的 `genix-infra/qrcode/convertor/QRCodeConvertor.java` 和 `QRCodeMapper.java`

## 5. 删除依赖

- [x] 5.1 从 `genix-application/build.gradle.kts` 删除 `implementation(project(":mumu-services:mumu-genix:genix-infra"))`

## 6. 验证

- [x] 6.1 运行 `./gradlew :mumu-services:mumu-genix:genix-application:build` 确认编译通过
