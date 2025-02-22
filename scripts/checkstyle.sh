#!/bin/bash

# 获取当前脚本所在的目录（scripts 文件夹）
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)

# 切换到项目根目录（确保执行 Gradle 的时候是从根目录）
cd "$SCRIPT_DIR/.."

# 运行 Checkstyle 任务（适用于整个项目）
./gradlew checkstyleMain

# 递归查找所有模块的 Checkstyle 报告（main.xml 和 main.html）
find "$SCRIPT_DIR/../" -path "*/build/reports/checkstyle/main.xml" | while read -r CHECKSTYLE_XML_REPORT; do
    CHECKSTYLE_HTML_REPORT="${CHECKSTYLE_XML_REPORT%.xml}.html" # 对应的 HTML 报告路径

    # 检查 XML 文件是否存在
    if [ -f "$CHECKSTYLE_XML_REPORT" ]; then
        # 判断 XML 是否包含 `<violation>`（如果存在违规信息）
        if ! grep -q "<violation" "$CHECKSTYLE_XML_REPORT"; then
            echo "✅ $(dirname "$CHECKSTYLE_XML_REPORT") 没有 Checkstyle 违规，删除无用报告..."
            rm -f "$CHECKSTYLE_XML_REPORT" "$CHECKSTYLE_HTML_REPORT"
        else
            echo "❌ $(dirname "$CHECKSTYLE_XML_REPORT") 发现 Checkstyle 违规，保留报告。"
        fi
    fi
done
