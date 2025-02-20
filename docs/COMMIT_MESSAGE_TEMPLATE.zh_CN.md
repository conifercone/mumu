- [简体中文](COMMIT_MESSAGE_TEMPLATE.zh_CN.md)
- [繁體中文](COMMIT_MESSAGE_TEMPLATE.zh_TW.md)
- [English](../COMMIT_MESSAGE_TEMPLATE.md)
- [日本語](COMMIT_MESSAGE_TEMPLATE.ja.md)
- [한국어](COMMIT_MESSAGE_TEMPLATE.ko.md)

## 提交信息模板

```text
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

## 说明

- `<type>`：表示提交的类型，可以是以下之一：
    - feat：新功能
    - fix：修复问题
    - docs：文档更新
    - style：代码样式（不影响功能的更改）
    - refactor：重构代码（功能没有改变）
    - perf：性能优化
    - test：测试相关更改
    - build：构建相关更改
    - ci：CI配置修改
    - chore：杂项更改
    - revert：撤销更改

- `<scope>`：表示影响的模块或功能，可以是小写字母，也可以包含中文。例如：`user`, `auth`, `登录功能`。

- `<subject>`：提交的简短描述，最大长度为50个字符。描述应该简洁、清晰，并且第一字母大写。

- `<body>`：可选部分，填写更详细的描述，最多72个字符一行，解释为什么需要做这个更改，如何解决问题，以及是否有副作用。

- `<footer>`：可选部分，通常包含与该提交相关的票据链接，例如：`Closes #11`。如果是破坏性更新，应该在此处注明，例如：
  `BREAKING CHANGE: 内容`。
