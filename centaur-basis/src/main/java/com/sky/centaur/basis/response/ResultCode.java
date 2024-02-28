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
package com.sky.centaur.basis.response;

import com.sky.centaur.basis.tools.SpringContextUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 响应码
 *
 * @author 单开宇
 * @since 2024-01-20
 */
public enum ResultCode implements BaseResultInterface {
  SUCCESS(200),
  BAD_REQUEST(400),
  UNAUTHORIZED(401),
  NOT_FOUND(404),
  INTERNAL_SERVER_ERROR(500),
  METHOD_NOT_ALLOWED(405),
  /*参数错误:1001-1999*/
  PARAMS_IS_INVALID(1001),
  PARAMS_IS_BLANK(1002),
  /*认证错误2001-2999*/
  ACCOUNT_ALREADY_EXISTS(2001),
  ACCOUNT_NAME_CANNOT_BE_EMPTY(2002),
  ACCOUNT_PASSWORD_CANNOT_BE_EMPTY(2003),
  ACCOUNT_PASSWORD_IS_INCORRECT(2004),
  ACCOUNT_DOES_NOT_EXIST(2005),
  UNSUPPORTED_GRANT_TYPE(2006),
  INVALID_CLIENT(2007),
  INVALID_GRANT(2008),
  INVALID_SCOPE(2009),
  INVALID_TOKEN(2010),
  /*数据源错误3001-3999*/
  MISSING_P6SPY_DEPENDENCY_INFORMATION(3001),
  /*数据转换错误4001-4999*/
  OPERATION_LOG_KAFKA_MESSAGE_CONVERSION_FAILED(4001);
  private final Integer code;
  private final MessageSource messageSource = SpringContextUtil.getBean(MessageSource.class);

  ResultCode(int code) {
    this.code = code;
  }

  @Contract(pure = true)
  @Override
  public @NotNull String getResultCode() {
    return this.code.toString();
  }

  @Override
  public @NotNull String getResultMsg() {
    return messageSource.getMessage(getResultCode(), null, LocaleContextHolder.getLocale());
  }
}
