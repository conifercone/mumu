![centaur](./logo.png)

# centaur

![GitHub Created At](https://img.shields.io/github/created-at/conifercone/centaur)
![GitHub repo size](https://img.shields.io/github/repo-size/conifercone/centaur)
![GitHub top language](https://img.shields.io/github/languages/top/conifercone/centaur)
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
│  ├─ 📂 centaur-authentication -- authentication service
│  │  │- 📂 authentication-adapter -- authentication service adaptation layer
│  │  │- 📂 authentication-application -- authentication service application layer
│  │  │- 📂 authentication-client -- authentication service client
│  │  │- 📂 authentication-domain -- authentication service domain layer
│  │  │- 📂 authentication-infrastructure -- authentication service infrastructure layer
│  │─ 📂 centaur-basis -- basic module
│  │─ 📂 centaur-extension -- expansion module
│  │─ 📂 centaur-log -- log service
│  │  │- 📂 log-adapter -- log service adaptation layer
│  │  │- 📂 log-application -- log service application layer
│  │  │- 📂 log-client -- log service client
│  │  │- 📂 log-domain -- log service domain layer
│  │  │- 📂 log-infrastructure -- log service infrastructure layer
│  │─ 📂 centaur-unique -- unique data generation service
│  │  │- 📂 unique-adapter -- unique data generation service adaptation layer
│  │  │- 📂 unique-application -- unique data generation service application layer
│  │  │- 📂 unique-client -- unique data generation service client
│  │  │- 📂 unique-domain -- unique data generation service domain layer
│  │  │- 📂 unique-infrastructure -- Unique data generation service infrastructure layer
```

## Build

### infrastructure

| name          | releases |
|---------------|----------|
| apisix        | 3.8.0    |
| elasticsearch | 8.12.0   |
| kafka         | 3.4      |
| postgresql    | 15.1     |
| redis         | latest   |
| zookeeper     | 3.8      |
| consul        | 1.15.4   |
| zipkin        | 3.19.0   |

## Dependency Statement

[**Dependency graph**](https://github.com/conifercone/centaur/network/dependencies)

## License

[Apache License 2.0](LICENSE) © kaiyu.shan@outlook.com
