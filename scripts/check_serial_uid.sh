#!/bin/bash

# 查找当前目录及其子目录中包含 serialVersionUID 的 .java 文件，且路径中包含 src/main/java 或 src/test/java
find ../ -type f \( -path "*/src/main/java/*" -o -path "*/src/test/java/*" \) -name "*.java" -exec grep -H "serialVersionUID" {} \; | \
awk -F'[=;]' '
{
    # 获取文件名和 serialVersionUID
    serial = $2;
    gsub(/ /, "", serial);
    file = $1;

    # 检查是否已经遇到过相同的 serialVersionUID
    if (serial in seen) {
        print "Duplicate serialVersionUID: " serial " found in " seen[serial] " and " file;
    } else {
        seen[serial] = file;
    }
}'
