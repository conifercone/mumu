# 更新日志

- [简体中文](CHANGELOG.zh_CN.md)
- [English](../CHANGELOG.md)

该项目的所有显着更改都将记录在该文件中。

格式基于[Keep a Changelog](https://keepachangelog.com/en/1.1.0/)，
并且该项目遵循[语义版本控制](https://semver.org/spec/v2.0.0.html)。

## [未发布]

### 新增

### 变更

### 移除

## [2.2.0] - 2024-10-24

### 新增

- 统一响应结果增加traceId字段。
- 统一响应结果增加时间戳字段。
- 账户角色关系、角色权限关系增加缓存。
- 当前登录账户信息查询接口增加缓存。
- 账户新增分页查询接口。
- 新增下线用户接口。
- 新增退出登录接口。
- 新增项目启动成功监听器。
- 新增账户系统设置。
- 角色增加缓存。
- 根据ID查询权限增加缓存。
- 客户端模块增加项目信息打印。
- 新增根据ID获取账户基本信息接口。
- 账户ID新增不等于0校验。
- 角色新增归档数据查询接口。
- 新增HttpMessageNotReadableException全局异常处理。
- 已归档权限新增不查询总数的分页查询。
- 新增检查序列化ID是否存在重复的脚本。
- 权限新增不查询总数的分页查询。
- 角色查询增加角色相关权限详细信息返回。
- 角色新增不查询总数的分页查询。
- MapStruct mapper统一增加unmappedTargetPolicy = ReportingPolicy.IGNORE。

### 修复

- 修复update_license_current_year.sh执行后可能导致文件内容乱码问题。

### 变更

- 规范接口参数，降低复杂度。
- 优化grpc接口。
- 日志保留策略调整。
- 优化账户查询结果。
- io.swagger.core.v3:swagger-annotations-jakarta升级至2.2.25。
- flyway升级至10.20.0。
- org.jobrunr:jobrunr-spring-boot-3-starter升级至7.3.1。
- 更新README文档中基础设施部分说明。
- 规范类名和接口方法名。
- 更换图标。
- 完善账户接口参数注释。
- 分页查询当前页默认从1开始。
- 按照restful规范重构接口。
- 页码参数重命名为current。
- 优化账户登录性能。
- 在线用户数量统计逻辑优化。
- CustomDescription注解重命名为Meta、GenerateDescription注解重命名为Metamodel。
- kotlin升级至2.0.21。
- org.apache.curator:curator-recipes升级至5.7.1。
- org.jetbrains:annotations升级至26.0.1。
- 接口参数由List类型修改为Collection类型。
- redis-om-spring升级至0.9.6。
- BaseClientObject日期属性格式修改为符合按照ISO-8601标准。
- 优化多语言获取逻辑防止NPE。
- 根据数据库范式重构文本广播消息表及对应逻辑。
- io.hypersistence:hypersistence-utils-hibernate-63升级至3.8.3。
- com.google.guava:guava-bom升级至33.3.1-jre。
- 账户性别&语言类型修改为varchar消除数据库差异。
- 更新注解处理器提示信息。

### 移除

- 移除不常用且用途危险的grpc方法。
- 删除认证相关重复配置。

## [2.1.0] - 2024-09-30

### 新增

- 新增条件执行器。
- 新增条件注解。
- 获取当前登录账户信息接口增加账户角色权限信息返回。
- 注解处理器增加版本信息生成。
- grpc增加服务发现客户端名称解析器。
- 增加flyway插件。
- 新增检查并设置环境变量脚本。
- 新增license脚本。
- 删除账户&删除账户归档数据时同时删除账户地址数据。
- 项目版本（开发、测试、预发布）增加git hash值标识。
- 新增限流拓展功能。
- 新增根据ID删除订阅消息、广播消息归档数据定时任务。
- 新增根据ID删除角色、账户归档数据定时任务。
- 新增根据ID删除权限归档数据定时任务。
- 危险操作注解value属性增加参数替换功能。

### 修复

- 修复根据ID更新用户角色接口时用户地址为空问题。

### 变更

- 按照数据库范式重构账户和角色映射关系，允许账户同时拥有多个角色。
- 账户支持添加多个地址。
- 按照数据库范式重构角色和权限映射关系。
- collections4 CollectionUtils替换spring CollectionUtils。
- 更新flyway脚本位置。
- gradle版本升级至8.10.2。
- 统一认证端点处理器。
- grpc版本升级至1.68.0。
- deepl-java升级至1.6.0。
- commons-io升级至2.17.0。
- 内置环境变量名修改为小写。
- 修改jpa扫描范围。
- springboot升级至3.3.4。
- protobuf升级至4.28.2。
- 修改Rsa#jksKeyPair默认值。
- 完善账户注册grpc接口参数属性。
- flyway升级至10.18.0。
- mapstruct升级至1.6.2。
- 更新SECURITY文档内容。
- log4j2设置UTF-8为默认编码。
- 优化项目结构。
- 优化权限归档定时任务执行逻辑。

### 移除

- 统一认证端点处理器去除日志自动上传功能降低架构复杂度。
- 删除暂时不用的插件。

## [2.0.0] - 2024-09-06

### 新增

- 添加了中文版的 README 文档。
- 添加了中文版的贡献指南。
- 添加了人脸检测功能。
- 添加了 OCR 扩展功能。
- 添加了根据省或州 ID 获取省或州、根据省或州 ID 获取省或州（包括下级城市）、根据城市 ID 获取省或州的功能。
- 添加了根据国家 ID 获取省或州信息、根据省或州 ID 获取城市信息的接口。
- 添加了获取国家详细信息的接口（不包含省、州、城市信息）。
- 添加了获取国家详细信息的接口。
- 添加了全球地理数据 JSON 文件。
- 添加了新建账户和添加地址的接口。
- 为账户添加了地址属性。
- 添加了数据脱敏工具类。
- 添加了危险操作的注释和切面。
- 为与角色权限相关的操作添加了危险操作注解。
- 在角色归档时增加了判断是否正在使用，不能归档。
- 增加了归档时的权限，判断是否正在使用。
- 添加了分页查询归档权限的接口。

### 修复

### 变更

- 项目重命名。
- 优化单元测试逻辑。
- 消除重复常量。
- 阿里云机器翻译 Bean 初始化增加判断。
- 统一依赖名称。
- 更换图标。
- 将 protobufBomVersion 从 3.25.3 升级到 4.28.0。
- 使用 commons-lang3 的 StringUtils 替换 spring 的 StringUtils。
- 为相关实体添加了序列化接口。

### 移除

## [1.0.4] - 2024-08-27

### 新增

- 添加了 PR 徽章。
- 添加了国际化信息。
- 添加了贡献者列表。
- 添加了标签操作。
- 添加了问候操作。
- 添加了详细的异常信息打印功能。
- gRPC 方法权限增加了配置文件配置方式。
- 新增了获取当前服务器时间的接口。
- 添加了二维码相关功能。
- 添加了条形码相关功能。
- 添加了注解处理器以实现类描述信息生成功能。
- 为 jar 任务的清单文件添加了 Application-Version。
- Spring Boot 的 bootJar 任务添加了签名。
- Spring Boot 的 bootJar 任务添加了许可证文件打包。
- 添加了归档的基本属性。
- 新增了归档表的触发器。
- 文本订阅消息新增了根据 ID 从归档中恢复消息的功能。
- 添加了归档和从归档中恢复的权限。
- 权限的新增、删除和修改兼容归档逻辑。
- 为角色添加了归档和恢复功能。
- 为账户新增了归档和恢复功能。
- 添加了 Slack 徽章。

### 修复

- 修复权限验证异常。

### 变更

- 修改了慢 SQL 表格式。
- 修改了慢 SQL 统计阈值。
- 优化了非空过滤逻辑。
- 在日志中屏蔽敏感信息。
- 统一了权限校验逻辑。
- 更换图标。
- Gradle 版本升级到 8.10。
- 更新了消息服务的数据库触发函数和触发器。
- Spring Boot 版本升级到 3.3.3。
- Kotlin 版本升级到 2.0.20。
- Flyway 版本升级到 10.17.2。
- redis-om-spring 版本升级到 0.9.5。
- MapStruct 版本升级到 1.6.0。
- Guava 版本升级到 33.3.0-jre。
- Minio 版本升级到 8.5.12。

### 移除

- 全局排除 tomcat。
- 消息服务消息状态删除存档属性。

## [1.0.3] - 2024-08-07

### 新增

- 添加了自定义 JKS 密钥功能。
- 添加了 `NotBlankOrNull` 校验注解。
- `CommonConstants` 添加了私有构造函数。
- 为账户模型添加了年龄属性。
- 为账户添加了生日属性。
- 添加了慢 SQL 统计功能。
- 添加了 `project-report` 插件。
- 添加了 `IllegalArgumentException` 全局异常处理。
- 添加了签名插件。
- 添加了机器翻译功能。
- 文本订阅消息新增了查询所有和某个人的消息记录功能。
- 文本广播消息转发增加了接收者验证。
- 添加了基于 ID 的文本广播消息归档功能。
- 添加了基于 ID 的文本订阅消息归档功能。
- 添加了文本订阅和广播消息归档表。
- 新增索引。
- 新增了文本广播消息的触发器。
- 文本订阅消息新增了基于 ID 的未读消息接口。
- 客户端对象转换添加了后处理。
- 添加了 `BeanNameConstants`。
- 文本订阅消息新增了查询当前用户发送的所有消息的接口。
- 新增了顶级客户端对象的基本属性。
- 文本广播消息新增了查询当前用户发送的所有消息的接口。
- 添加了基于 ID 删除文本广播消息的功能。
- 添加了基于 ID 读取文本广播消息的功能。
- 添加了基于 ID 删除文本订阅消息的功能。
- 添加了基于 ID 阅读文本订阅消息的功能。

### 修复

- 修复权限验证异常。
- 修复拼写错误。

### 变更

- 修改 GitHub Actions 的默认分支为 `develop`。
- 规范 `libs.versions.toml` 的键值命名。
- `spring-cloud` 升级到 2023.0.3。
- 全局排除 `logback`。
- `redis-om-spring` 升级到 0.9.4。
- 添加了基于 ID 阅读文本订阅消息的限制。
- 添加了基于 ID 阅读文本广播消息的限制。
- `SubscriptionTextMessageRepository#findByIdAndReceiverId` 参数添加了 `NotNull` 注解。
- 统一修改了 `EnableRedisDocumentRepositories` 注解的范围。
- 分页查询统一添加了页码和当前页码参数值的校验。
- 优化了订阅和广播通道的存储逻辑。
- 将 `group` 和 `version` 提取到 `gradle.properties` 文件中。

### 移除

## [1.0.2] - 2024-07-19

### 新增

- 集成了 `redis-om-spring` 注解处理器。
- 异常提示内容适应用户语言偏好。
- 为权限相关函数添加了参数校验。
- 权限模块增加了 `refresh_token` 的 Redis 存储和有效性验证。
- 权限模块增加了客户端令牌的 Redis 存储和验证。
- 权限模块新增了数据初始化脚本。
- 消息模块和权限模块集成了 `jobrunr-spring-boot-3-starter`。
- 新增了消息模块。
- 消息模块增加了 WebSocket Netty 实现。
- 消息模块实现了订阅文本消息转发功能。
- 消息模块实现了广播文本消息发布功能。

### 修复

- 修复权限名称格式提示信息错误。
- 修复token有效性验证失败的问题。

### 变更

- 权限代码添加了唯一约束。
- 修改了 gRPC 同步调用方法。
- 更新了权限 gRPC 接口单元测试逻辑，以确保完整性和独立性。
- 为角色代码添加了唯一性验证。
- 为账户邮箱地址添加了唯一性验证。
- 更新权限时，判断更新的代码是否已存在。
- 更新账户时，检查更新的邮箱地址是否已存在。
- 更新角色时，添加了对代码的唯一性检查。
- 将 Lombok Gradle 插件修改为 `latest.release`。
- 更新账户时，验证更新后的账户名是否唯一。
- 统一了认证服务数据库表索引名称命名规范。
- 在密码认证下，将 `principalName` 更改为 `username`。
- 客户端令牌结合了角色权限和客户端自身的权限。
- 调整了日志文件大小的上限至 250MB。
- 将 Gradle 版本升级到 8.9。
- 操作日志和系统日志的 Kafka 主题名称及 Elasticsearch 索引名称提取到 `LogProperties`。
- 账户被禁用或删除时，清除当前账户的登录信息。
- `PgSqlFunctionNameConstants` 添加了 `final` 访问修饰符。
- Gradle 从 Groovy 迁移到 Kotlin。

### 移除

- 删除 log4j2 OnStartupTriggeringPolicy 策略。
- 删除gradle jvmargs中的-Xmx、-XX:MaxMetaspaceSize配置。

## [1.0.1] - 2024-06-28

### 新增

- 唯一数据生成服务新增了代码生成和验证功能。
- 添加了邮件服务。
- 邮件服务中新增了模板邮件通知功能。
- 添加了文件服务。
- 文件服务增加了流式文件上传、下载、删除和获取文本格式文件内容的功能。
- 为账户添加了语言偏好和时区属性。
- 唯一数据生成服务中新增了获取可用时区列表的接口。
- 新增了短信模块。

### 修复

- 修复了事务无效的问题。
- 修复了国际化异常提示错误。

### 变更

- 账户注册功能新增了时区有效性检查。
- 账户注册功能新增了验证码检查。
- 修改了用户表、权限表和角色表的数据库列为 `NOT_NULL`，并添加了相应的默认值。
- 账户注册 gRPC 接口参数属性修改为包装类。
- 修改了 gRPC 通道关闭逻辑。
- 集成了 MapStruct 以替代原有的 Bull 进行对象转换。
- 删除了当前账户功能，并添加了验证码验证。
- 令牌声明中新增了账户语言偏好属性。
- Spring Boot 升级到 3.3.1。
- `redis-om-spring` 升级到 0.9.3。
- `hypersistence-utils-hibernate-63` 升级到 3.7.7。

### 移除

- 删除 Flyway gradle 插件。
- sql 文件删除许可证。

## [1.0.0] - 2024-06-13

### 新增

- 身份验证服务器。
- 资源服务器客户端。
- 操作日志收集功能。
- 系统日志收集功能。
- 分布式唯一主键生成。
- 基于zookeeper的分布式锁。

[//]: # (@formatter:off)
[未发布]: https://github.com/conifercone/mumu/compare/v2.2.0...develop
[2.2.0]: https://github.com/conifercone/mumu/compare/v2.1.0...v2.2.0
[2.1.0]: https://github.com/conifercone/mumu/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/conifercone/mumu/compare/v1.0.4...v2.0.0
[1.0.4]: https://github.com/conifercone/mumu/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/conifercone/mumu/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/conifercone/mumu/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/conifercone/mumu/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/conifercone/mumu/releases/tag/v1.0.0
[//]: # (@formatter:on)