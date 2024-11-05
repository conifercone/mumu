# Changelog

- [简体中文](docs/CHANGELOG.zh_CN.md)
- [English](CHANGELOG.md)

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Removed

## [2.2.0] - 2024-10-24

### Added

- Add traceId field to unified response results.
- Add a timestamp field to unified response results.
- Account role relationships and role permission relationships are cached.
- Add cache to the current login account information query interface.
- A new paging query interface for accounts has been added.
- Added offline user interface.
- Added a new logout interface.
- Added new project startup success listener.
- Added new account system settings.
- Added character cache.
- Increase caching based on ID query permissions.
- The client module adds project information printing.
- Added an interface to obtain basic account information based on ID.
- The new account ID is not equal to 0 verification.
- Added archive data query interface for roles.
- Added HttpMessageNotReadableException global exception handling.
- Added paging query without querying the total number for archived permissions.
- Added a script to check whether serialized IDs are duplicated.
- Added permission to add paging query without querying the total number.
- Role query adds role-related permission details returned.
- The role has added paging query that does not query the total number.
- MapStruct mapper uniformly adds unmappedTargetPolicy = ReportingPolicy.IGNORE.

### Fixed

- Fixed the issue that the file content may be garbled after execution of
  update_license_current_year.sh.

### Changed

- Standardize interface parameters and reduce complexity.
- Optimize grpc interface.
- Log retention policy adjustment.
- Optimize account query results.
- io.swagger.core.v3:swagger-annotations-jakarta upgraded to 2.2.25.
- flyway upgraded to 10.20.0.
- org.jobrunr:jobrunr-spring-boot-3-starter upgraded to 7.3.1.
- Updated the description of the infrastructure section in the README document.
- Standardize class names and interface method names.
- Change icon.
- Improve account interface parameter comments.
- Pagination query for the current page starts from 1 by default.
- Reconstruct the interface according to RESTful specifications.
- The page number parameter is renamed to current.
- Optimize account login performance.
- Logic optimization of online user number statistics.
- The CustomDescription annotation is renamed to Meta, and the GenerateDescription annotation is
  renamed to Metamodel.
- kotlin upgraded to 2.0.21.
- org.apache.curator:curator-recipes upgraded to 5.7.1.
- org.jetbrains:annotations upgraded to 26.0.1.
- The interface parameters are changed from List type to Collection type.
- redis-om-spring upgraded to 0.9.6.
- BaseClientObject date attribute format modified to comply with ISO-8601 standard.
- Optimize multi-language acquisition logic to prevent NPE.
- Reconstruct the text broadcast message table and corresponding logic according to the database
  paradigm.
- io.hypersistence:hypersistence-utils-hibernate-63 is upgraded to 3.8.3.
- com.google.guava:guava-bom is upgraded to 33.3.1-jre.
- Change the account gender & language type to varchar to eliminate database differences.
- Update annotation processor prompt information.

### Removed

- Removed uncommon and dangerous grpc methods.
- Delete authentication-related duplicate configurations.

## [2.1.0] - 2024-09-30

### Added

- Added conditional executor.
- Added conditional annotation.
- Get current login account information interface to add account role permission information return.
- The annotation processor adds version information generation.
- grpc adds service discovery client name resolver.
- Added flyway plug-in.
- Added script to check and set environment variables.
- Added license script.
- When deleting an account & deleting the account archive data, the account address data will also
  be deleted.
- Added git hash value identification to project versions (development, testing, pre-release).
- Added current limit expansion function.
- Added scheduled tasks for deleting subscription messages and broadcast message archiving data
  based on ID.
- Added scheduled tasks to delete roles and account archive data based on ID.
- Added a new scheduled task for archiving data based on ID deletion permission.
- The value attribute of the dangerous operation annotation adds parameter substitution function.

### Fixed

- Fixed the problem that the user address is empty when updating the user role interface based on
  ID.

### Changed

- Reconstruct the account and role mapping relationship according to the database paradigm, allowing
  accounts to have multiple roles at the same time.
- Account supports adding multiple addresses.
- Reconstruct role and permission mapping relationships according to database paradigm.
- collections4 CollectionUtils replaces spring CollectionUtils.
- Update flyway script location.
- Gradle version upgraded to 8.10.2.
- Unified authentication endpoint processor.
- grpc version upgraded to 1.68.0.
- deepl-java upgraded to 1.6.0.
- commons-io upgraded to 2.17.0.
- The built-in environment variable names are changed to lowercase.
- Modify jpa scanning range.
- springboot upgraded to 3.3.4.
- protobuf upgraded to 4.28.2.
- Modify the default value of Rsa#jksKeyPair.
- Improve account registration grpc interface parameter attributes.
- flyway upgraded to 10.18.0.
- mapstruct upgraded to 1.6.2.
- Update SECURITY document content.
- log4j2 sets UTF-8 as the default encoding.
- Optimize project structure.
- Optimize the execution logic of permission archiving scheduled tasks.

### Removed

- The unified authentication endpoint processor removes the automatic log upload function to reduce
  architectural complexity.
- Delete plug-ins that are temporarily unused.

## [2.0.0] - 2024-09-06

### Added

- Added Chinese version of readme document.
- Added Chinese version of contribution guide.
- Added face detection function.
- Added ocr expansion function.
- Added the ability to obtain a province or state based on the province or state ID, obtain the
  province or state (including lower-level cities) based on the province or state ID, and obtain the
  province or state based on the city ID.
- Added interfaces for obtaining province or state information based on country ID and obtaining
  city information based on province or state ID.
- Added an interface to obtain detailed information about the country (excluding province, state,
  and city information).
- Added interface for obtaining detailed country information.
- Added global geographic data json file.
- Add new account and add address interface.
- Added address attribute to account.
- Added data desensitization tool class.
- Added notes and aspects of dangerous operations.
- Add dangerous operation annotations for operations related to role permissions.
- The character archive has been added to determine whether it is in use and cannot be archived.
- The permissions for archiving have been increased to determine whether archiving is in use.
- Added paging query archived permissions interface.

### Fixed

### Changed

- Project rename.
- Optimize unit test logic.
- eliminate duplicate constants.
- Alibaba Cloud machine translation bean initialization adds judgment.
- Unify dependency names.
- Change icon.
- bump protobufBomVersion from 3.25.3 to 4.28.0.
- commons-lang3 StringUtils replaces spring StringUtils.
- Added serialization interface for related entities.

### Removed

## [1.0.4] - 2024-08-27

### Added

- Add pr badge.
- Added internationalization information.
- Add Contributors.
- Add label action.
- Add Greetings action.
- Add detailed exception information printing.
- grpc method permissions increase configuration file configuration method.
- Added new interface to obtain current server time.
- Added QR code related functions.
- Added barcode related functions.
- Added annotation processor to implement class description information generation function.
- Added Application-Version to the jar task manifest.
- springboot bootJar task adds signature.
- Springboot bootJar task adds license file packaging.
- Added archived basic attributes.
- New trigger for archive table.
- Text subscription messages have a new function of restoring messages from archives based on ID.
- Added permissions for archiving and restoring from archives.
- Permission addition, deletion and modification are compatible with archiving logic.
- Added archiving and restoring functions to roles.
- Added new archiving and recovery functions for accounts.
- Added slack badge.

### Fixed

- Fix permission verification exception.

### Changed

- Modify slow sql table format.
- Modify slow sql statistics threshold.
- Optimize non-empty filtering logic.
- Block sensitive information in logs.
- Unified permission verification logic.
- Change icon.
- Gradle version upgraded to 8.10.
- Update message service database trigger functions and triggers.
- The springboot version is upgraded to 3.3.3.
- Kotlin version upgraded to 2.0.20.
- flyway version upgraded to 10.17.2.
- redis-om-spring version upgraded to 0.9.5.
- mapstruct version upgraded to 1.6.0.
- The guava version is upgraded to 33.3.0-jre.
- minio version upgraded to 8.5.12.

### Removed

- Exclude tomcat globally.
- Message service message status delete archived attribute.

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
[unreleased]: https://github.com/conifercone/mumu/compare/v2.2.0...develop
[2.2.0]: https://github.com/conifercone/mumu/compare/v2.1.0...v2.2.0
[2.1.0]: https://github.com/conifercone/mumu/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/conifercone/mumu/compare/v1.0.4...v2.0.0
[1.0.4]: https://github.com/conifercone/mumu/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/conifercone/mumu/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/conifercone/mumu/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/conifercone/mumu/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/conifercone/mumu/releases/tag/v1.0.0
[//]: # (@formatter:on)
