/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sky.centaur.extension.processor.response;

/**
 * 响应码
 *
 * @author 单开宇
 * @since 2024-01-20
 */
public enum ResultCode implements BaseResultInterface {
  SUCCESS(200, "成功"),
  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "认证失败"),
  NOT_FOUND(404, "接口不存在"),
  INTERNAL_SERVER_ERROR(500, "系统繁忙"),
  METHOD_NOT_ALLOWED(405, "方法不被允许"),
  /*参数错误:1001-1999*/
  PARAMS_IS_INVALID(1001, "参数无效"),
  PARAMS_IS_BLANK(1002, "参数为空"),
  /*账户错误2001-2999*/
  ACCOUNT_ALREADY_EXISTS(2001, "账户已存在");
  private final Integer code;
  private final String message;

  ResultCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public String getResultCode() {
    return this.code.toString();
  }

  @Override
  public String getResultMsg() {
    return this.message;
  }
}
