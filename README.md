![mumu](./logo.svg)

- [简体中文](README.zh_CN.md)
- [English](README.md)

# mumu

![GitHub Created At](https://img.shields.io/github/created-at/conifercone/mumu)
![GitHub repo size](https://img.shields.io/github/repo-size/conifercone/mumu)
![GitHub top language](https://img.shields.io/github/languages/top/conifercone/mumu)
[![JDK version](https://img.shields.io/badge/JDK-21+-green.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
![GitHub Release](https://img.shields.io/github/v/release/conifercone/mumu)
[![GitHub License](https://img.shields.io/github/license/conifercone/mumu)](https://github.com/conifercone/mumu)
[![GitHub issues](https://img.shields.io/github/issues/conifercone/mumu)](https://github.com/conifercone/mumu/issues)
[![GitHub Pull Requests](https://img.shields.io/github/issues-pr/conifercone/mumu)](https://github.com/conifercone/mumu/pulls)
[![GitHub language count](https://img.shields.io/github/languages/count/conifercone/mumu)](https://github.com/conifercone/mumu)
[![GitHub last commit](https://img.shields.io/github/last-commit/conifercone/mumu/develop)](https://github.com/conifercone/mumu)
![GitHub Discussions](https://img.shields.io/github/discussions/conifercone/mumu)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/conifercone/mumu/dependency-submission.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/conifercone/mumu/badge/develop)](https://www.codefactor.io/repository/github/conifercone/mumu/overview/develop)
[![Slack](https://img.shields.io/badge/Slack-Join%20Our%20Community-green)](https://join.slack.com/t/mumu-community/shared_invite/zt-2ov97fcpj-bFJZmpXSp5YZWSU9zD7S5g)

## Content list

- [Projects](#Projects)
- [Module Introduction](#module-introduction)
- [Build](#Build)
- [Dependency Statement](#dependency-statement)
- [License](#license)
- [Contributors](#contributors)

## Projects

## Module Introduction

```text
mumu
│  ├─ 📂 mumu-services -- mumu services
│  │  ├─ 📂 mumu-authentication -- authentication service
│  │  │  │- 📂 authentication-adapter -- authentication service adaptation layer
│  │  │  │- 📂 authentication-application -- authentication service application layer
│  │  │  │- 📂 authentication-client -- authentication service client
│  │  │  │- 📂 authentication-domain -- authentication service domain layer
│  │  │  │- 📂 authentication-infrastructure -- authentication service infrastructure layer
│  │  │─ 📂 mumu-file -- file service
│  │  │  │- 📂 file-adapter -- file service adaptation layer
│  │  │  │- 📂 file-application -- file service application layer
│  │  │  │- 📂 file-client -- file service client
│  │  │  │- 📂 file-domain -- file service domain layer
│  │  │  │- 📂 file-infrastructure -- file service infrastructure layer
│  │  │─ 📂 mumu-log -- log service
│  │  │  │- 📂 log-adapter -- log service adaptation layer
│  │  │  │- 📂 log-application -- log service application layer
│  │  │  │- 📂 log-client -- log service client
│  │  │  │- 📂 log-domain -- log service domain layer
│  │  │  │- 📂 log-infrastructure -- log service infrastructure layer
│  │  │─ 📂 mumu-mail -- mail service
│  │  │  │- 📂 mail-adapter -- mail service adaptation layer
│  │  │  │- 📂 mail-application -- mail service application layer
│  │  │  │- 📂 mail-client -- mail service client
│  │  │  │- 📂 mail-domain -- mail service domain layer
│  │  │  │- 📂 mail-infrastructure -- mail service infrastructure layer
│  │  │─ 📂 mumu-message -- message service
│  │  │  │- 📂 message-adapter -- message service adaptation layer
│  │  │  │- 📂 message-application -- message service application layer
│  │  │  │- 📂 message-client -- message service client
│  │  │  │- 📂 message-domain -- message service domain layer
│  │  │  │- 📂 message-infrastructure -- message service infrastructure layer
│  │  │─ 📂 mumu-sms -- sms service
│  │  │  │- 📂 sms-adapter -- sms service adaptation layer
│  │  │  │- 📂 sms-application -- sms service application layer
│  │  │  │- 📂 sms-client -- sms service client
│  │  │  │- 📂 sms-domain -- sms service domain layer
│  │  │  │- 📂 sms-infrastructure -- sms service infrastructure layer
│  │  │─ 📂 mumu-unique -- unique data generation service
│  │  │  │- 📂 unique-adapter -- unique data generation service adaptation layer
│  │  │  │- 📂 unique-application -- unique data generation service application layer
│  │  │  │- 📂 unique-client -- unique data generation service client
│  │  │  │- 📂 unique-domain -- unique data generation service domain layer
│  │  │  │- 📂 unique-infrastructure -- Unique data generation service infrastructure layer
│  │─ 📂 mumu-basis -- basic module
│  │─ 📂 mumu-extension -- expansion module
│  │─ 📂 mumu-processor -- processor module
```

## Build

### infrastructure

| name          | releases                     |
|---------------|------------------------------|
| apisix        | 3.8.0                        |
| elasticsearch | 8.12.0                       |
| kafka         | 3.4                          |
| postgresql    | 16.3                         |
| redis         | latest                       |
| zookeeper     | 3.8                          |
| consul        | 1.15.4                       |
| minio         | RELEASE.2024-06-13T22-53-53Z |
| zipkin        | 3.19.0                       |

## Dependency Statement

[**Dependency graph**](https://github.com/conifercone/mumu/network/dependencies)

## License

[Apache License 2.0](LICENSE) © <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>

## Contributors

<a href="https://github.com/conifercone/mumu/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=conifercone/mumu"  alt="https://github.com/conifercone/mumu/graphs/contributors"/>
</a>
