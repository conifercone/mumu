# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- v1.0.3 Added read interface for subscribing messages.
- v1.0.3 A new read interface has been added to the broadcast message.
- v1.0.3 A new archiving interface is added to the subscription message.

### Changed

- v1.0.3 Broadcast text messages have new read and unread attributes.

### Removed

## [1.0.2] - 2024-07-19

### Added

- integrate redis-om-spring annotation processor
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
- Under password authentication, principalName is changed to the user name.
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
[unreleased]: https://github.com/conifercone/centaur/compare/v1.0.2...develop
[1.0.2]: https://github.com/conifercone/centaur/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/conifercone/centaur/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/conifercone/centaur/releases/tag/v1.0.0
[//]: # (@formatter:on)
