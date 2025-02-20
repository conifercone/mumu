- [简体中文](docs/COMMIT_MESSAGE_TEMPLATE.zh_CN.md)
- [繁體中文](docs/COMMIT_MESSAGE_TEMPLATE.zh_TW.md)
- [English](COMMIT_MESSAGE_TEMPLATE.md)
- [日本語](docs/COMMIT_MESSAGE_TEMPLATE.ja.md)
- [한국어](docs/COMMIT_MESSAGE_TEMPLATE.ko.md)

## Commit Message Template

```text
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

## Explanation

- `<type>`: indicates the type of the commit, which can be one of the following:
    - feat: New features
    - fix: fix the problem
    - docs: Document update
    - style: code style (no changes that affect functionality)
    - refactor: refactor code (function has not changed)
    - perf: Performance optimization
    - test: test related changes
    - build: build related changes
    - ci: CI configuration modification
    - chore: Miscellaneous changes
    - revert: Undo the change

- `<scope>`: The module or function that indicates the influence, can be in lowercase letters or can
  contain Chinese. For example: `user`, `auth`, `Login function`.

- `<subject>`: A short description of the submission, with a maximum length of 50 characters. The
  description should be concise and clear, and the first letter should be capitalized.

- `<body>`: Optional section, fill in a more detailed description, up to 72 characters per line,
  explaining why this change needs to be made, how to solve the problem, and whether there are side
  effects.

- `<footer>`: Optional section, usually containing a ticket link related to the submission, for
  example: `Closes #11`. If it is a destructive update, it should be noted here, for example:
  `BREAKING CHANGE: Content`.

