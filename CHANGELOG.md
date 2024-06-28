# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- v1.0.2 SMS integrates Alibaba Cloud and Huawei Cloud SMS platform.
- v1.0.2 SMS implements SMS verification code sending function.
- v1.0.2 SMS adds a new option to switch SMS platforms based on configuration files.

### Changed

- v1.0.2 Added SMS verification code for account registration.
- v1.0.2 Internationalization exceptions prompt switching of abnormal content according to account
  language preference.

### Removed

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
[unreleased]: https://github.com/conifercone/centaur/compare/v1.0.1...develop
[1.0.1]: https://github.com/conifercone/centaur/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/conifercone/centaur/releases/tag/v1.0.0
[//]: # (@formatter:on)
