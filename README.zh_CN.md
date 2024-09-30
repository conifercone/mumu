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

## 内容列表

- [项目](#项目)
- [模块介绍](#模块介绍)
- [构建](#构建)
- [依赖声明](#依赖声明)
- [许可](#许可)
- [贡献者](#贡献者)

## 项目

## 模块介绍

```text
mumu
│  ├─ 📂 mumu-services -- mumu 服务
│  │  ├─ 📂 mumu-authentication -- 认证服务
│  │  │  │- 📂 authentication-adapter -- 认证服务适配层
│  │  │  │- 📂 authentication-application -- 认证服务应用层
│  │  │  │- 📂 authentication-client -- 认证服务客户端
│  │  │  │- 📂 authentication-domain -- 认证服务领域层
│  │  │  │- 📂 authentication-infrastructure -- 认证服务基础设施层
│  │  │─ 📂 mumu-file -- 文件服务
│  │  │  │- 📂 file-adapter -- 文件服务适配层
│  │  │  │- 📂 file-application -- 文件服务应用层
│  │  │  │- 📂 file-client -- 文件服务客户端
│  │  │  │- 📂 file-domain -- 文件服务领域层
│  │  │  │- 📂 file-infrastructure -- 文件服务基础设施层
│  │  │─ 📂 mumu-log -- 日志服务
│  │  │  │- 📂 log-adapter -- 日志服务适配层
│  │  │  │- 📂 log-application -- 日志服务应用层
│  │  │  │- 📂 log-client -- 日志服务客户端
│  │  │  │- 📂 log-domain -- 日志服务领域层
│  │  │  │- 📂 log-infrastructure -- 日志服务基础设施层
│  │  │─ 📂 mumu-mail -- 邮件服务
│  │  │  │- 📂 mail-adapter -- 邮件服务适配层
│  │  │  │- 📂 mail-application -- 邮件服务应用层
│  │  │  │- 📂 mail-client -- 邮件服务客户端
│  │  │  │- 📂 mail-domain -- 邮件服务领域层
│  │  │  │- 📂 mail-infrastructure -- 邮件服务基础设施层
│  │  │─ 📂 mumu-message -- 消息服务
│  │  │  │- 📂 message-adapter -- 消息服务适配层
│  │  │  │- 📂 message-application -- 消息服务应用层
│  │  │  │- 📂 message-client -- 消息服务客户端
│  │  │  │- 📂 message-domain -- 消息服务领域层
│  │  │  │- 📂 message-infrastructure -- 消息服务基础层
│  │  │─ 📂 mumu-sms -- 短信服务
│  │  │  │- 📂 sms-adapter -- 短信业务适配层
│  │  │  │- 📂 sms-application -- 短信服务应用层
│  │  │  │- 📂 sms-client -- 短信服务客户端
│  │  │  │- 📂 sms-domain -- 短信服务领域层
│  │  │  │- 📂 sms-infrastructure -- 短信服务基础设施层
│  │  │─ 📂 mumu-unique -- 唯一服务
│  │  │  │- 📂 unique-adapter -- 唯一服务适配层
│  │  │  │- 📂 unique-application -- 唯一服务应用层
│  │  │  │- 📂 unique-client -- 唯一服务客户端
│  │  │  │- 📂 unique-domain -- 唯一服务领域层
│  │  │  │- 📂 unique-infrastructure -- 唯一服务基础设施层
│  │─ 📂 mumu-basis -- 基本模块
│  │─ 📂 mumu-extension -- 扩展模块
│  │─ 📂 mumu-processor -- 处理器模块
```

## 构建

### 基础设施

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

## 依赖声明

[**Dependency graph**](https://github.com/conifercone/mumu/network/dependencies)

## 许可

[Apache License 2.0](LICENSE) © <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>

## 贡献者

<a href="https://github.com/conifercone/mumu/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=conifercone/mumu"  alt="https://github.com/conifercone/mumu/graphs/contributors"/>
</a>
