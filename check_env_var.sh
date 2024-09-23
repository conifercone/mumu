#!/bin/bash

# 指定要检查的环境变量名集合
ENV_VARS=("MUMU_SIGNING_KEY_ID" "MUMU_SIGNING_KEY" "MUMU_SIGNING_PASSWORD" "consul_token")

# 遍历环境变量集合
for VAR in "${ENV_VARS[@]}"; do
    if [ -z "${!VAR}" ]; then
        echo "错误: 环境变量 '$VAR' 不存在。"
        read -p "是否要设置 '$VAR'? (y/n): " SET_VAR

        if [[ "$SET_VAR" == "y" || "$SET_VAR" == "Y" ]]; then
            read -p "请输入 '$VAR' 的值并按 Enter: " VALUE
            export "$VAR=$VALUE"
            echo "环境变量 '$VAR' 已设置为: $VALUE"
        else
            echo "环境变量 '$VAR' 未设置。"
        fi
    else
        echo "环境变量 '$VAR' 存在，值为: ${!VAR}"
    fi
done
