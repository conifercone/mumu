## Commit Message Template

```text
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

## Construction

Where `<type>` is one of feat, fix, docs, style, refactor, perf, test, build, ci, chore, revert.
`<scope>` may contain Chinese characters and should be lowercase.
`<subject>` should start with a capital letter and may contain Chinese characters.
It should also be a brief description (maximum 50 characters).
`<type>(<scope>): <subject>` part must be included.
`<body>` this is optional, 72-character wrapped. This should answer: Why was this change necessary?
How does it address the problem? Are there any side effects?
`<footer>` this is optional, include a link to the ticket, if any. e.g.: Closes #11, contains
destructive updates, if any. e.g.: BREAKING CHANGE: destructive update content.
