# Contributing to MuMu

- [简体中文](docs/CONTRIBUTING.zh_CN.md)
- [English](CONTRIBUTING.md)

👍🎉 First off, thanks for taking the time to contribute! 🎉👍

This project adheres to the Contributor Covenant [code of conduct](CODE_OF_CONDUCT.md).
By participating, you are expected to uphold this code. Please report unacceptable
behavior to kaiyu.shan@outlook.com.

The following is a set of guidelines for contributing to MuMu.
These are just guidelines, not rules, use your best judgment and feel free to
propose changes to this document in a pull request.

## Version naming convention

There are indeed some recognized unified specifications for version naming specifications, the most
famous of which is Semantic Versioning (SemVer). Semantic versioning provides a standardized set of
rules for the format and use of version numbers, aiming to help developers understand the nature and
impact of version changes.

### Semantic Versioning

The specification for **Semantic Versioning** defines the following version number format:

```
MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]
```

- **MAJOR**: Major version number. When you make incompatible API modifications, increase this
  version number.
- **MINOR**: Minor version number, increment this version number when you add features while
  maintaining backward compatibility.
- **PATCH**: Revision number, increase this version number when you make backward compatibility bug
  fixes.
- **PRERELEASE**: Optional prerelease identifier, identifying an unfinished or experimental version.
- **BUILD**: Optional build metadata that provides additional information about the build.

**Example**:

- `1.0.0`: initial release version.
- `1.1.0`: Backwards compatible features added.
- `1.0.1`: Backwards compatible fixes.
- `2.0.0`: Incompatible API changes.
- `1.0.0-alpha`: pre-release version.
- `1.0.0+build5678`: version containing build metadata.

In Semantic Versioning (SemVer), if feature additions and fixes are made at the same time, the
version number update should be determined based on the type of change. Here are principles and
suggestions for handling this situation:

### Principles for updating version numbers

1. **Priority**:
    - If there are multiple types of changes (such as feature additions and fixes), the version
      number should be updated based on the most important part of the change.
    - Generally, feature additions have higher priority than fixes, so if there are feature
      additions and fixes at the same time, the version number update is mainly determined based on
      the feature additions.

2. **Revision Number (PATCH)**:
    - If only fixes are made and no new features are added or incompatible changes are made, the
      revision number (PATCH) should be updated.

3. **Minor version number (MINOR)**:
    - If feature additions are made and backward compatibility is maintained, but no incompatible
      changes are made, the minor version number (MINOR) should be updated.

4. **Main Version Number (MAJOR)**:
    - If incompatible changes are made, whether feature additions or fixes, the MAJOR should be
      updated.

### Practical examples

- **Feature Additions and Fixes**:
    - **Feature added** (to maintain backward compatibility): Update minor version number.
    - **Fix** (backward compatibility): The revision number can be increased, but in the case of
      feature additions, the increase in the minor version number will cover the fixed content.
    - **Example**: Assume the current version is `1.0.0`. If new features are added and bugs are
      fixed, you should update the version number to `1.1.0` instead of `1.0.1`. This is because
      feature additions are usually more important than fixes, and `1.1.0` already implies fixes.

- **Incompatible changes at the same time**:
    - If there are still incompatible API changes, the MAJOR should be updated.
    - **Example**: If the current version is `1.0.0` and incompatible API changes, feature additions
      and fixes are made, the version number should be updated to `2.0.0`.

### Comprehensive consideration

If the version update contains multiple types of changes, it is common practice to determine the
version number update based on the primary type of change. Feature additions typically push the
minor version number up, while incompatible changes push the major version number up.

Ensure that all important changes are accurately reflected in the version number, and document the
specific changes in the release notes to help users understand the nature and impact of the version
update.

**Prerelease identifier**:

In semantic versioning (Semantic Versioning), the pre-release label (Pre-release label) is used to
indicate a specific pre-release status of a version. These versions are usually still in the testing
phase or have not yet been completed. The pre-release identifier helps users distinguish versions at
different stages and provides additional information.

### Common pre-release logos

1. **alpha**:
    - **Description**: Indicates an early development version, which usually contains unfinished
      features, may be unstable, and is mainly used for internal testing or early feedback.
    - **Example**: `1.0.0-alpha`

2. **beta**:
    - **Description**: Indicates a version that is basically functional but may still have problems.
      It is usually used for extensive testing and may contain some known issues or defects.
    - **Example**: `1.0.0-beta`

3. **rc** (Release Candidate):
    - **Description**: Indicates a release candidate version, usually a version close to official
      release, used for final testing. If no major issues are found, this version will likely become
      an official stable version.
    - **Example**: `1.0.0-rc1`

4. **snapshot**:
    - **Description**: Indicates an ongoing development version, usually a frequently updated
      version that may be released at various stages of development to test the latest changes.
    - **Example**: `1.0.0-snapshot`

5. **dev** (Development):
    - **Description**: Indicates a version under development, usually used to mark a version under
      development, which may include unstable features or unfinished work.
    - **Example**: `1.0.0-dev`

6. **test**:
    - **Description**: Represents the version in the testing phase, used to verify specific
      functions of the software or conduct integration testing.
    - **Example**: `1.0.0-test`

7. **pre** (Pre-release):
    - **Description**: A common pre-release identifier, indicating that the version is before the
      official release, usually used in various pre-release stages.
    - **Example**: `1.0.0-pre`

### Usage of pre-release logo

- Pre-release tags should be preceded by a hyphen `-` and the tag name after the version number, for
  example `1.0.0-alpha`.
- Can contain numbers and letters to identify different pre-release versions. For example,
  `1.0.0-beta2` means the second beta version.
- The version marked as pre-release does not affect the version sorting; when sorting, the
  pre-release version will be considered earlier than the official version.

**Example**:

- `1.0.0-alpha` < `1.0.0-beta` < `1.0.0-rc1` < `1.0.0`

Using the pre-release badge can help development teams and users identify the development stage of a
release and determine whether it is suitable for production use. Make sure to document pre-release
features and known issues in detail in the release notes to help users make choices.

### Main principles of semantic versioning

1. **Increment of version number**: When incompatible API changes occur, the major version number is
   increased; when new features are added and backward compatibility is maintained, the minor
   version number is increased; when backward compatible fixes are made, The revision number is
   increased.
2. **Pre-release and build metadata**: Pre-release identification and build metadata do not affect
   the sorting of version numbers and are only used to provide additional information.

### Other version control specifications

This project uses additional Git Short Hash to improve version control:

1. **Git Short Hash**: Use Git short commit hash as part of the version number, especially during
   development or continuous integration. Example: 1.0.0-dev-fe456874.

### Official reference

- **Semantic Versioning Official Specification**: [Semantic Versioning 2.0.0](https://semver.org/)

These specifications help developers understand the impact of version changes and ensure that
project versions are clear and consistent. Choose the appropriate specification based on project
needs and ensure the team agrees on version naming rules.
