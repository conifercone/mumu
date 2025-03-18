![mumu](../logo.svg)

- [简体中文](README.zh_CN.md)
- [繁體中文](README.zh_TW.md)
- [English](../README.md)
- [日本語](README.ja.md)
- [한국어](README.ko.md)

# mumu

[![GitHub Created At](https://img.shields.io/github/created-at/conifercone/mumu)](https://github.com/conifercone/mumu)
[![GitHub repo size](https://img.shields.io/github/repo-size/conifercone/mumu)](https://github.com/conifercone/mumu)
[![GitHub top language](https://img.shields.io/github/languages/top/conifercone/mumu)](https://github.com/conifercone/mumu)
[![JDK version](https://img.shields.io/badge/JDK-23+-green.svg)](https://jdk.java.net/23)
[![GitHub Release](https://img.shields.io/github/v/release/conifercone/mumu)](https://github.com/conifercone/mumu/releases/latest)
[![GitHub License](https://img.shields.io/github/license/conifercone/mumu)](https://github.com/conifercone/mumu)
[![GitHub issues](https://img.shields.io/github/issues/conifercone/mumu)](https://github.com/conifercone/mumu/issues)
[![GitHub Pull Requests](https://img.shields.io/github/issues-pr/conifercone/mumu)](https://github.com/conifercone/mumu/pulls)
[![GitHub language count](https://img.shields.io/github/languages/count/conifercone/mumu)](https://github.com/conifercone/mumu)
[![GitHub last commit](https://img.shields.io/github/last-commit/conifercone/mumu/develop)](https://github.com/conifercone/mumu)
[![GitHub Discussions](https://img.shields.io/github/discussions/conifercone/mumu)](https://github.com/conifercone/mumu/discussions)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/conifercone/mumu/pmd.yml?label=PMD)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/conifercone/mumu/checkstyle.yml?label=Checkstyle)
[![CodeFactor](https://www.codefactor.io/repository/github/conifercone/mumu/badge/develop)](https://www.codefactor.io/repository/github/conifercone/mumu/overview/develop)
[![Slack](https://img.shields.io/badge/Slack-Join%20Our%20Community-green)](https://join.slack.com/t/mumu-community/shared_invite/zt-2ov97fcpj-bFJZmpXSp5YZWSU9zD7S5g)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/conifercone/mumu)
![GitHub contributors](https://img.shields.io/github/contributors/conifercone/mumu)

## 목차

- [프로젝트](#프로젝트)
- [모듈 소개](#모듈-소개)
- [빌드](#빌드)
- [의존성 선언](#의존성-선언)
- [라이선스](#라이선스)
- [기여자](#기여자)

## 프로젝트

> 이미 많은 성숙한 백엔드 관리 시스템이 존재하지만, 저는 코드의 청결함에 대한 강박이 있어서
> 적합한 프로젝트를 찾지 못했습니다. 그래서 스스로 코드가 깔끔하고 기능이 완비된 백엔드 관리 시스템을
> 오픈 소스로 개발하기로 결정했습니다. 이 프로젝트의 이름은 mumu 입니다. 제 아들(沐沐)의 이름에서 따왔으며,
> 아들이 건강하게 자라길 바라는 마음처럼, mumu 프로젝트도 튼튼하게 성장하길 바랍니다.
> 제 생애 동안 본 프로젝트는 개인과 조직에게 무료로 오픈 소스로 제공될 것입니다.

### 🎉 즐겁게 사용할 수 있는 깔끔한 관리 시스템! 🎉

복잡한 설정과 어지러운 코드에 지치셨나요?  
**즉시 사용 가능한 관리 시스템**을 확인해 보세요! 🎁  
상자를 열면, 슈퍼스타처럼 쉽게 관리할 수 있습니다. 청결한 코드가 빛나고 있습니다! ✨

🌟 **이 시스템을 사랑하게 될 이유:**

- **즉각적인 설정**: 마법 주문이 필요 없습니다. 연결만 하면 작동합니다!
- **청결한 코드**: 별을 주고 싶을 만큼 깔끔합니다!
- **사용자 친화적**: 금붕어도 사용할 수 있을 정도로 간단합니다!

복잡함과 이별하고, 즐거운 관리의 여정을 시작하세요! 🚀🎈

## 모듈 소개

```text
mumu
├── mumu-services
│   ├── mumu-authentication
│   ├── mumu-file
│   ├── mumu-log
│   ├── mumu-mail
│   ├── mumu-message
│   ├── mumu-sms
│   └── mumu-unique
├── mumu-basis
├── mumu-extension
└── mumu-processor
```

## 빌드

### 기본 인프라

| 이름            | 릴리즈                          |
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
| mongodb       | 8.0.1                        |

## 의존성 선언

[**Dependency graph**](https://github.com/conifercone/mumu/network/dependencies)

## 라이선스

[Apache License 2.0](../LICENSE) © <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>

## 기여자

<a href="https://github.com/conifercone/mumu/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=conifercone/mumu"  alt="https://github.com/conifercone/mumu/graphs/contributors"/>
</a> 
