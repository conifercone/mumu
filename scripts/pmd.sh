#!/bin/bash

# 获取当前脚本所在的目录（假设脚本在 scripts 文件夹）
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)

# 切换到项目根目录（确保执行 Gradle 的时候是从根目录）
cd "$SCRIPT_DIR/.."

# 运行 PMD 任务（适用于整个项目）
./gradlew pmdMain

# 递归查找所有模块的 PMD 报告（main.xml 和 main.html）
find "$SCRIPT_DIR/../" -path "*/build/reports/pmd/main.xml" | while read -r PMD_XML_REPORT; do
    PMD_HTML_REPORT="${PMD_XML_REPORT%.xml}.html" # 对应的 HTML 报告路径

    # 检查 XML 是否存在
    if [ -f "$PMD_XML_REPORT" ]; then
        # 判断 XML 是否包含 `<violation>`（如果存在违规信息）
        if ! grep -q "<violation" "$PMD_XML_REPORT"; then
            echo "✅ $(dirname "$PMD_XML_REPORT") 没有 PMD 违规，删除无用报告..."
            rm -f "$PMD_XML_REPORT" "$PMD_HTML_REPORT"
        else
            echo "❌ $(dirname "$PMD_XML_REPORT") 发现 PMD 违规，保留报告。"
        fi
    fi
done
