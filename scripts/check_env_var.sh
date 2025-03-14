#!/bin/bash

# 指定要检查的环境变量名集合
ENV_VARS=("MUMU_SIGNING_KEY_ID" "MUMU_SIGNING_KEY" "MUMU_SIGNING_PASSWORD" "CONSUL_TOKEN")

# 遍历环境变量集合
for VAR in "${ENV_VARS[@]}"; do
    if [ -z "${!VAR}" ]; then
        echo "Error: Environment variable '$VAR' does not exist."
    else
        echo "The environment variable '$VAR' exists and the value is: ${!VAR}"
    fi
done
