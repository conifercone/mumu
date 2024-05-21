![centaur](./logo.png)

# centaur

[![JDK version](https://img.shields.io/badge/JDK-21+-green.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![centaur version](https://img.shields.io/badge/centaur-1.0.0--SNAPSHOT-brightgreen)](https://github.com/conifercone/centaur)
[![GitHub License](https://img.shields.io/github/license/conifercone/centaur)](https://github.com/conifercone/centaur)
[![GitHub issues](https://img.shields.io/github/issues/conifercone/centaur)](https://github.com/conifercone/centaur)
[![GitHub language count](https://img.shields.io/github/languages/count/conifercone/centaur)](https://github.com/conifercone/centaur)
[![GitHub last commit](https://img.shields.io/github/last-commit/conifercone/centaur/develop)](https://github.com/conifercone/centaur)
![GitHub Discussions](https://img.shields.io/github/discussions/conifercone/centaur)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/conifercone/centaur/dependency-submission.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/conifercone/centaur/badge)](https://www.codefactor.io/repository/github/conifercone/centaur)

## Content list

- [Projects](#Projects)
- [Module Introduction](#module-introduction)
- [Build](#Build)
- [Dependency Statement](#dependency-statement)
- [License](#license)

## Projects

## Module Introduction

```text
centaur
│  ├─ 📂 centaur-authentication -- 鉴权服务
│  │  │- 📂 authentication-adapter -- 鉴权服务适配层
│  │  │- 📂 authentication-application -- 鉴权服务应用层
│  │  │- 📂 authentication-client -- 鉴权服务客户端
│  │  │- 📂 authentication-domain -- 鉴权服务领域层
│  │  │- 📂 authentication-infrastructure -- 鉴权服务基础设施层
│  │─ 📂 centaur-basis -- 基础模块
│  │─ 📂 centaur-extension -- 拓展模块
│  │─ 📂 centaur-log -- 日志服务
│  │  │- 📂 log-adapter -- 日志服务适配层
│  │  │- 📂 log-application -- 日志服务应用层
│  │  │- 📂 log-client -- 日志服务客户端
│  │  │- 📂 log-domain -- 日志服务领域层
│  │  │- 📂 log-infrastructure -- 日志服务基础设施层
│  │─ 📂 centaur-unique -- 唯一性数据生成服务
│  │  │- 📂 unique-adapter -- 唯一性数据生成服务适配层
│  │  │- 📂 unique-application -- 唯一性数据生成服务应用层
│  │  │- 📂 unique-client -- 唯一性数据生成服务客户端
│  │  │- 📂 unique-domain -- 唯一性数据生成服务领域层
│  │  │- 📂 unique-infrastructure -- 唯一性数据生成服务基础设施层
```

## Build

### infrastructure

| name          | releases |
|---------------|----------|
| apisix        | 3.8.0    |
| elasticsearch | 8.12.0   |
| kafka         | 3.4      |
| mysql         | 8.0.21   |
| postgresql    | 15.1     |
| redis         | latest   |
| zookeeper     | 3.8      |
| consul        | 1.15.4   |
| zipkin        | 3.19.0   |

## Dependency Statement

[**Dependency graph**](https://github.com/conifercone/centaur/network/dependencies)

## License

[Apache License 2.0](LICENSE) © kaiyu.shan@outlook.com

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=conifercone/centaur&type=Date)
