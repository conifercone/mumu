/*
 * Copyright (c) 2024-2025, the original author or authors.
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

import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.basis.kotlin.tools.SpringContextUtils;
import java.util.Locale;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 响应码
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public enum ResponseCode implements BaseResponse {
  SUCCESS("200", 200),
  BAD_REQUEST("400", 400),
  UNAUTHORIZED("401", 401),
  ACCESS_DENIED("403", 403),
  NOT_FOUND("404", 404),
  TOO_MANY_REQUESTS("429", 429),
  INTERNAL_SERVER_ERROR("500", 500),
  METHOD_NOT_ALLOWED("405", 405),
  REQUEST_HAS_BEEN_PROCESSED("409", 409),
  /*参数错误:1001-1999*/
  PARAMS_IS_INVALID("1001", 400),
  PRIMARY_KEY_CANNOT_BE_EMPTY("1005", 400),
  REQUEST_MISSING_NECESSARY_PARAMETERS("1006", 400),
  /*认证错误2001-2999*/
  ACCOUNT_ALREADY_EXISTS("2001", 500),
  ACCOUNT_NAME_CANNOT_BE_EMPTY("2002", 500),
  ACCOUNT_PASSWORD_CANNOT_BE_EMPTY("2003", 500),
  ACCOUNT_PASSWORD_IS_INCORRECT("2004", 500),
  ACCOUNT_DOES_NOT_EXIST("2005", 500),
  UNSUPPORTED_GRANT_TYPE("2006", 500),
  INVALID_CLIENT("2007", 500),
  INVALID_GRANT("2008", 500),
  INVALID_SCOPE("2009", 500),
  INVALID_TOKEN("2010", 500),
  ACCOUNT_DISABLED("2011", 500),
  ACCOUNT_LOCKED("2012", 500),
  PASSWORD_EXPIRED("2013", 500),
  ACCOUNT_HAS_EXPIRED("2014", 500),
  DIGITAL_SIGNATURE_AUTHENTICATION_FAILED("2016", 500),
  /*grpc错误3001-3999*/
  GRPC_SERVICE_NOT_FOUND("3001", 500),
  /*数据转换错误4001-4999*/
  OPERATION_LOG_KAFKA_MESSAGE_CONVERSION_FAILED("4001", 500),
  /*拓展模块错误5001-5999*/
  FAILED_TO_OBTAIN_DISTRIBUTED_LOCK("5001", 500),
  FAILED_TO_RELEASE_DISTRIBUTED_LOCK("5002", 500),
  /*业务错误6001-7999*/
  PERMISSION_IS_IN_USE_AND_CANNOT_BE_REMOVED("6001", 500),
  ROLE_IS_IN_USE_AND_CANNOT_BE_REMOVED("6002", 500),
  ROLE_DOES_NOT_EXIST("6003", 500),
  FILE_CONTENT_CANNOT_BE_EMPTY("6004", 500),
  STORAGE_ZONE_CANNOT_BE_EMPTY("6005", 500),
  FILE_DOWNLOAD_FAILED("6006", 500),
  FILE_NAME_CANNOT_BE_EMPTY("6007", 500),
  FILE_UPLOAD_FAILED("6008", 500),
  INPUT_STREAM_CONVERSION_FAILED("6009", 500),
  FILE_DOES_NOT_EXIST("6010", 500),
  THE_FILE_STORAGE_ADDRESS_DOES_NOT_EXIST("6011", 500),
  FILE_STORAGE_ADDRESS_CREATION_FAILED("6012", 500),
  FILE_METADATA_INVALID("6013", 500),
  FILE_METADATA_PERSIST_FAILED("6014", 500),
  DATA_CONVERSION_FAILED("6015", 500),
  VERIFICATION_CODE_GENERATION_FAILED("6016", 500),
  VERIFICATION_CODE_VALIDITY_PERIOD_CANNOT_BE_EMPTY("6017", 500),
  VERIFICATION_CODE_LENGTH_NEEDS_TO_BE_GREATER_THAN_0("6018", 500),
  VERIFICATION_CODE_INCORRECT("6019", 500),
  VERIFICATION_CODE_VERIFICATION_EXCEPTION("6020", 500),
  TIME_ZONE_IS_NOT_AVAILABLE("6021", 500),
  FILE_DELETION_FAILED("6022", 500),
  VERIFICATION_CODE_ID_CANNOT_BE_EMPTY("6023", 500),
  VERIFICATION_CODE_CANNOT_BE_EMPTY("6024", 500),
  PERMISSION_CODE_OR_ID_ALREADY_EXISTS("6025", 500),
  ROLE_CODE_OR_ID_ALREADY_EXISTS("6026", 500),
  PERMISSION_CODE_ALREADY_EXISTS("6027", 500),
  ACCOUNT_EMAIL_ALREADY_EXISTS("6028", 500),
  ROLE_CODE_ALREADY_EXISTS("6029", 500),
  ACCOUNT_NAME_ALREADY_EXISTS("6030", 500),
  THE_INITIAL_PASSWORD_CANNOT_BE_EMPTY("6032", 500),
  PERMISSION_IS_IN_USE_AND_CANNOT_BE_ARCHIVE("6033", 500),
  ROLE_IS_IN_USE_AND_CANNOT_BE_ARCHIVE("6034", 500),
  ACCOUNT_CONVERSION_TO_ARCHIVED_FAILED("6035", 500),
  UNABLE_TO_OBTAIN_CURRENT_REQUESTED_IP("6036", 500),
  PERMISSION_DOES_NOT_EXIST("6038", 500),
  OCR_RECOGNITION_FAILED("6039", 500),
  TRANSLATION_FAILED("6040", 500),
  QR_CODE_GENERATION_FAILED("6041", 500),
  BARCODE_GENERATION_FAILED("6042", 500),
  GEOGRAPHIC_DATA_LOADING_FAILED("6043", 500),
  PERMISSION_CYCLE("6044", 500),
  PERMISSION_PATH_ALREADY_EXISTS("6045", 500),
  DESCENDANT_PERMISSION_HAS_DESCENDANT_PERMISSION("6046", 500),
  ROLE_CYCLE("6047", 500),
  ROLE_PATH_ALREADY_EXISTS("6048", 500),
  DESCENDANT_ROLE_HAS_DESCENDANT_ROLE("6049", 500),
  FAILURE_TO_GET_INFORMATION_RELATED_TO_THE_LOGIN_ACCOUNT("6050", 500),
  THE_ACCOUNT_HAS_AN_UNUSED_BALANCE("6051", 500),
  INVALID_PHONE_NUMBER("6052", 500),
  ACCOUNT_ARCHIVE_NOT_FOUND("6053", 500),
  STORAGE_ZONE_INVALID("6054", 500),
  THE_STORAGE_ZONE_DOES_NOT_EXIST("6055", 500),
  INVALID_PERMISSION_FORMAT("6056", 500),
  INVALID_ROLE_FORMAT("6057", 500),
  INVALID_ACCOUNT_FORMAT("6058", 500);
  private final String code;
  @Getter
  private final int status;

  ResponseCode(String code, int status) {
    this.code = code;
    this.status = status;
  }

  @Contract(pure = true)
  @Override
  public @NotNull String getCode() {
    return this.code;
  }

  @Override
  public @NotNull String getMessage() {
    return makeMessage(null);
  }

  @Override
  public @NotNull String getMessage(@Nullable Object[] args) {
    return makeMessage(args);
  }

  private @NotNull String makeMessage(@Nullable Object[] args) {
    return SpringContextUtils.getBean(MessageSource.class)
      .map(messageSource -> messageSource.getMessage(getCode(), args,
        SecurityContextUtils.getLoginAccountLanguage()
          .map(languageEnum -> Locale.of(languageEnum.getCode()))
          .orElse(LocaleContextHolder.getLocale()))).orElse(getCode());
  }
}
