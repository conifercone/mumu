![mumu](./logo.svg)

- [简体中文](README.zh_CN.md)
- [繁體中文](README.zh_TW.md)
- [English](README.md)
- [日本語](README.ja.md)

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
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/conifercone/mumu/pmd.yml?label=PMD)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/conifercone/mumu/checkstyle.yml?label=Checkstyle)
[![CodeFactor](https://www.codefactor.io/repository/github/conifercone/mumu/badge/develop)](https://www.codefactor.io/repository/github/conifercone/mumu/overview/develop)
[![Slack](https://img.shields.io/badge/Slack-Join%20Our%20Community-green)](https://join.slack.com/t/mumu-community/shared_invite/zt-2ov97fcpj-bFJZmpXSp5YZWSU9zD7S5g)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/conifercone/mumu)
![GitHub contributors](https://img.shields.io/github/contributors/conifercone/mumu)

## 目錄

- [專案](#專案)
- [模組介紹](#模組介紹)
- [建置](#建置)
- [相依聲明](#相依聲明)
- [授權](#授權)
- [貢獻者](#貢獻者)

## 專案

> 雖然市面上已經有許多成熟的後台管理系統，但由於我對於整潔的程式碼有強烈的執念，所以一直找不到合適的專案。
> 因此決定自己動手開源一個程式碼整潔且功能完善的後台管理系統。這就是 mumu 的由來，mumu 名稱來自我的兒子（沐沐），
> 希望他能夠健康成長，同樣也希望 mumu 專案能夠茁壯成長。只要我在世，本專案將永遠免費開源，供個人和組織自由使用。

### 🎉 令人愉快的即用型整潔管理系統！ 🎉

對那些讓你頭疼的混亂程式碼和複雜設置感到厭倦了嗎？
來試試我們的 **即用型管理系統**！🎁
只需打開，它就會如超級巨星般輕鬆運行，程式碼潔淨得閃閃發亮！✨

🌟 **你會愛上它的理由：**

- **即時設置**：不需要任何魔法咒語，只需插上就能運行！
- **整潔程式碼**：乾淨到你會想給它一顆金星！
- **使用者友好**：即使你的金魚也能搞懂！

加入輕鬆管理的樂趣，和混亂說再見！讓管理變成一場愉快的旅程吧！🚀🎈

## 模組介紹

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

## 建置

### 基礎設施

| 名稱            | 版本                           |
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

## 相依聲明

[**相依圖**](https://github.com/conifercone/mumu/network/dependencies)

## 授權

[Apache License 2.0](LICENSE) © <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>

## 貢獻者

<a href="https://github.com/conifercone/mumu/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=conifercone/mumu"  alt="https://github.com/conifercone/mumu/graphs/contributors"/>
</a> 
