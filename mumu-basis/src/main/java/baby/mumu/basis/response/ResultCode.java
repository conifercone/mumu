/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.basis.response;

import baby.mumu.basis.kotlin.tools.SecurityContextUtil;
import baby.mumu.basis.kotlin.tools.SpringContextUtil;
import java.util.Locale;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 响应码
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public enum ResultCode implements BaseResultInterface {
  SUCCESS(200),
  BAD_REQUEST(400),
  UNAUTHORIZED(401),
  NOT_FOUND(404),
  TOO_MANY_REQUESTS(429),
  INTERNAL_SERVER_ERROR(500),
  METHOD_NOT_ALLOWED(405),
  /*参数错误:1001-1999*/
  PARAMS_IS_INVALID(1001),
  PARAMS_IS_BLANK(1002),
  DATA_ALREADY_EXISTS(1003),
  DATA_DOES_NOT_EXIST(1004),
  PRIMARY_KEY_CANNOT_BE_EMPTY(1005),
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
  ACCOUNT_DISABLED(2011),
  ACCOUNT_LOCKED(2012),
  PASSWORD_EXPIRED(2013),
  ACCOUNT_HAS_EXPIRED(2014),
  INSUFFICIENT_AUTHENTICATION(2015),
  /*grpc错误3001-3999*/
  GRPC_SERVICE_NOT_FOUND(3001),
  /*数据转换错误4001-4999*/
  OPERATION_LOG_KAFKA_MESSAGE_CONVERSION_FAILED(4001),
  /*拓展模块错误5001-5999*/
  FAILED_TO_OBTAIN_DISTRIBUTED_LOCK(5001),
  FAILED_TO_RELEASE_DISTRIBUTED_LOCK(5002),
  /*业务错误6001-7999*/
  AUTHORITY_IS_IN_USE_AND_CANNOT_BE_REMOVED(6001),
  ROLE_IS_IN_USE_AND_CANNOT_BE_REMOVED(6002),
  ROLE_DOES_NOT_EXIST(6003),
  FILE_CONTENT_CANNOT_BE_EMPTY(6004),
  FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY(6005),
  FILE_DOWNLOAD_FAILED(6006),
  FILE_NAME_CANNOT_BE_EMPTY(6007),
  FILE_UPLOAD_FAILED(6008),
  INPUT_STREAM_CONVERSION_FAILED(6009),
  FILE_DOES_NOT_EXIST(6010),
  THE_FILE_STORAGE_ADDRESS_DOES_NOT_EXIST(6011),
  FILE_STORAGE_ADDRESS_CREATION_FAILED(6012),
  EMAIL_SENDING_EXCEPTION(6013),
  FAILED_TO_OBTAIN_EMAIL_TEMPLATE(6014),
  DATA_CONVERSION_FAILED(6015),
  SIMPLE_CAPTCHA_GENERATION_FAILED(6016),
  SIMPLE_CAPTCHA_VALIDITY_PERIOD_CANNOT_BE_EMPTY(6017),
  SIMPLE_CAPTCHA_LENGTH_NEEDS_TO_BE_GREATER_THAN_0(6018),
  CAPTCHA_INCORRECT(6019),
  CAPTCHA_VERIFICATION_EXCEPTION(6020),
  TIME_ZONE_IS_NOT_AVAILABLE(6021),
  FILE_DELETION_FAILED(6022),
  CAPTCHA_ID_CANNOT_BE_EMPTY(6023),
  CAPTCHA_CANNOT_BE_EMPTY(6024),
  AUTHORITY_CODE_OR_ID_ALREADY_EXISTS(6025),
  ROLE_CODE_OR_ID_ALREADY_EXISTS(6026),
  AUTHORITY_CODE_ALREADY_EXISTS(6027),
  ACCOUNT_EMAIL_ALREADY_EXISTS(6028),
  ROLE_CODE_ALREADY_EXISTS(6029),
  ACCOUNT_NAME_ALREADY_EXISTS(6030),
  WEBSOCKET_SERVER_CONNECTION_FAILED(6031),
  THE_INITIAL_PASSWORD_CANNOT_BE_EMPTY(6032),
  AUTHORITY_IS_IN_USE_AND_CANNOT_BE_ARCHIVE(6033),
  ROLE_IS_IN_USE_AND_CANNOT_BE_ARCHIVE(6034),
  THE_ACCOUNT_ALREADY_HAS_AN_ADDRESS(6035),
  UNABLE_TO_OBTAIN_CURRENT_REQUESTED_IP(6036);
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
    return messageSource.getMessage(getResultCode(), null,
        SecurityContextUtil.getLoginAccountLanguage()
            .map(languageEnum -> Locale.of(languageEnum.name()))
            .orElse(LocaleContextHolder.getLocale()));
  }
}
