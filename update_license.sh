#!/bin/sh

# 获取当前年份
current_year=$(date +'%Y')
organization="the original author or authors"  # 修改为实际组织名称

# 获取脚本所在目录的路径
script_dir=$(dirname "$0")

# 读取 SOURCE_CODE_HEAD.txt 模板并替换 ${year} 和 ${organization} 为实际值
license_file="$script_dir/SOURCE_CODE_HEAD.txt"
license_text=$(sed "s/\${year}/$current_year/g" "$license_file")
license_text=$(echo "$license_text" | sed "s/\${organization}/$organization/g")

# 格式化 License 注释为块注释
formatted_license_text="/*
$(echo "$license_text" | sed 's/^/ * /')
 */"

# 更新未提交中添加或修改文件的license
for file in $(git --no-pager diff --name-only --diff-filter=AM --cached | grep -E "^.+\.(java|kt)$" | uniq); do
  # 检查文件是否包含 License 注释
  if grep -q "Copyright (c) 2024" "$file"; then
      sed -i "s/Copyright (c) 2024-[0-9]\{4\}/Copyright (c) 2024-$current_year/" "$file"
  else
    # 如果文件不包含 License，添加 License 注释到文件头
    echo -e "$formatted_license_text\n$(cat "$file")" > "$file"
  fi
done
