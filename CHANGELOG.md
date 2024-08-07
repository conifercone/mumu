# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- v1.0.4 Machine translation adds Microsoft Translate option.
- v1.0.4 Extension module adds rule engine expansion implementation.

### Changed

### Removed

## [1.0.3] - 2024-08-07

### Added

- Added custom jks key function.
- Added NotBlankOrNull verification annotation.
- CommonConstants adds private constructor.
- Added age attribute to account model.
- Added birthday attribute to account.
- Added slow sql statistics function.
- Added project-report plugin.
- Added IllegalArgumentException global exception handling.
- Added signature plugin.
- Added machine translation function.
- Text subscription messages have been added to query all and someone’s message records.
- Text broadcast message forwarding adds receiver verification.
- Added text broadcast message archiving function based on ID.
- Added the function of archiving text subscription messages based on ID.
- Added text subscription and broadcast message archive tables.
- New index.
- New trigger for text broadcast messages.
- Text subscription message adds unread message interface based on ID.
- Client object conversion adds post-processing.
- Added BeanNameConstants.
- Text subscription message adds a new interface for querying all current users to send messages.
- New basic properties for top-level client objects.
- Text broadcast message has a new interface for querying all current users sending messages.
- Added the ability to delete text broadcast messages based on ID.
- Added read text broadcast message based on ID.
- Added the ability to delete text subscription messages based on ID.
- Added the ability to subscribe to messages based on ID read text.

### Fixed

- Fix permission verification exception.
- Fix spelling errors.

### Changed

- Modify the default branch of GitHub actions to develop.
- Standardize libs.versions.toml key value naming.
- spring-cloud upgraded to 2023.0.3.
- Exclude logback globally.
- redis-om-spring upgraded to 0.9.4.
- Add restrictions based on ID read text subscription messages.
- Add restrictions on read text broadcast messages based on ID.
- SubscriptionTextMessageRepository#findByIdAndReceiverId parameter adds NotNull annotation.
- Unified modify the EnableRedisDocumentRepositories annotation range.
- Pagination query uniformly adds page number and current page number parameter value verification.
- Optimize subscription and broadcast channel storage logic.
- Group and version are extracted into the gradle.properties file.

### Removed

## [1.0.2] - 2024-07-19

### Added

- integrate redis-om-spring annotation processor.
- Exception prompt content adapts to user language preference.
- Added parameter verification for permission-related functions.
- The permission module adds refresh_token redis storage and validity verification.
- The permission module adds client token redis storage and verification.
- A new data initialization script is added to the permission module.
- The message module and permission module are integrated with jobrunr-spring-boot-3-starter.
- Added message module.
- The messaging module adds websocket netty implementation.
- The message module implements the subscription text message forwarding function.
- The message module implements the broadcast text message publishing function.

### Fixed

- Fixed permission name format prompt information error.
- Fixed the problem of token validity verification failure.

### Changed

- Permission code adds unique constraints.
- Modify the grpc synchronization calling method.
- Update the authority grpc interface unit test logic to ensure integrity and independence.
- Add unique verification to role code.
- Add unique verification to the account email address.
- When updating permissions, determine whether the updated code already exists.
- When updating an account, check whether the updated email address already exists.
- When the role is updated, a uniqueness check is added to the code.
- The lombok gradle plugin is modified to latest.release.
- When updating an account, verify whether the updated account name is unique.
- Unified authentication service database table index name naming convention.
- Under password authentication, principalName is changed to the username.
- The client token combines the permissions of the role and the permissions in the client itself.
- The upper limit of log file size is adjusted to 250MB.
- The gradle version is upgraded to 8.9.
- Operation logs and system logs kafka topic name & elasticsearch index name are extracted into
  LogProperties.
- When the account is disabled and deleted, the current account login information will be cleared.
- PgSqlFunctionNameConstants adds final access modifier.
- Gradle is migrated from groovy to kotlin.

### Removed

- Remove log4j2 OnStartupTriggeringPolicy policy.
- Delete the -Xmx, -XX:MaxMetaspaceSize configuration in gradle jvmargs.

## [1.0.1] - 2024-06-28

### Added

- Unique data generation service adds code generation, verify function.
- Add mail service.
- Add template email notification in mail service.
- Add file service.
- The file service adds streaming file upload, download, deletion, and obtaining file content in
  text format.
- Added language preference and time zone attributes to the account.
- A new interface for obtaining the list of available time zones has been added to the unique data
  generation service.
- Added sms module.

### Fixed

- Fix transaction does not take effect.
- Fix internationalization exception prompt error.

### Changed

- Account registration function adds time zone validity check.
- Account registration function adds verification code check.
- Modify the database columns of the user table, permission table, and role table to NOT_NULL, and
  add corresponding default values.
- The account registration grpc interface parameter attribute is modified to a wrapper class.
- Modify grpc channel closing logic.
- Integrate mapstruct to replace the original bull for object conversion.
- Delete current account function and add verification code verification.
- Token claims adds account language preference attribute.
- springboot upgraded to 3.3.1.
- redis-om-spring upgraded to 0.9.3.
- hypersistence-utils-hibernate-63 upgraded to 3.7.7.

### Removed

- Remove flyway gradle plugin.
- sql file remove license.

## [1.0.0] - 2024-06-13

### Added

- Authentication server.
- Resource Server Client.
- Operation log collection function.
- System log collection function.
- Distributed unique primary key generation.
- Distributed lock based on zookeeper.

[//]: # (@formatter:off)
[unreleased]: https://github.com/conifercone/centaur/compare/v1.0.3...develop
[1.0.3]: https://github.com/conifercone/centaur/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/conifercone/centaur/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/conifercone/centaur/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/conifercone/centaur/releases/tag/v1.0.0
[//]: # (@formatter:on)
