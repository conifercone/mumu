#!/bin/bash

# 指定要检查的环境变量名集合
ENV_VARS=("mumu_signing_key_id" "mumu_signing_key" "mumu_signing_password" "consul_token")

# 遍历环境变量集合
for VAR in "${ENV_VARS[@]}"; do
    if [ -z "${!VAR}" ]; then
        echo "错误: 环境变量 '$VAR' 不存在。"
    else
        echo "环境变量 '$VAR' 存在，值为: ${!VAR}"
    fi
done
