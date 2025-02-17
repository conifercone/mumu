#!/bin/bash

# 运行 PMD 任务（适用于整个项目）
./gradlew pmdMain

# 递归查找所有模块的 PMD 报告（main.xml 和 main.html）
find . -path "*/build/reports/pmd/main.xml" | while read -r PMD_XML_REPORT; do
    PMD_HTML_REPORT="${PMD_XML_REPORT%.xml}.html" # 对应的 HTML 报告路径

    # 检查 XML 是否存在
    if [ -f "$PMD_XML_REPORT" ]; then
        # 判断 XML 是否包含 `<violation>`
        if ! grep -q "<violation" "$PMD_XML_REPORT"; then
            echo "✅ $(dirname "$PMD_XML_REPORT") 没有 PMD 违规，删除无用报告..."
            rm -f "$PMD_XML_REPORT" "$PMD_HTML_REPORT"
        else
            echo "❌ $(dirname "$PMD_XML_REPORT") 发现 PMD 违规，保留报告。"
        fi
    fi
done
