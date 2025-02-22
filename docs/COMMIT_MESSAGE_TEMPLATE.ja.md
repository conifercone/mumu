- [简体中文](COMMIT_MESSAGE_TEMPLATE.zh_CN.md)
- [繁體中文](COMMIT_MESSAGE_TEMPLATE.zh_TW.md)
- [English](../COMMIT_MESSAGE_TEMPLATE.md)
- [日本語](COMMIT_MESSAGE_TEMPLATE.ja.md)
- [한국어](COMMIT_MESSAGE_TEMPLATE.ko.md)

## コミットメッセージテンプレート

```text
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

## 説明

- `<type>`：コミットの種類を示し、以下の中から選択可能です：
    - feat：新機能
    - fix：問題修正
    - docs：ドキュメント更新
    - style：コードスタイル（機能に影響しない変更）
    - refactor：コードのリファクタリング（機能は変更なし）
    - perf：パフォーマンス最適化
    - test：テスト関連の変更
    - build：ビルド関連の変更
    - ci：CI設定の変更
    - chore：雑多な変更
    - revert：変更の取り消し

- `<scope>`：影響を受けるモジュールまたは機能を表し、小文字のアルファベットまたは日本語が使用可能です。例えば：
  `user`、`auth`、`ログイン機能`。

- `<subject>`：コミットの簡潔な説明で、最大50文字。説明は簡潔かつ明確で、最初の文字を大文字にしてください。

- `<body>`：任意項目。より詳細な説明を記載し、1行あたり最大72文字。この変更が必要な理由、問題の解決方法、副作用の有無などを説明します。

- `<footer>`：任意項目。通常、このコミットに関連するチケットリンクを含みます。例えば：`Closes #11`
  。破壊的変更がある場合はここで明記してください。例えば：
  `BREAKING CHANGE: 内容`。
