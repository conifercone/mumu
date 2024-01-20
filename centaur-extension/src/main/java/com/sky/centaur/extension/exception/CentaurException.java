package com.sky.centaur.extension.exception;

import com.sky.centaur.extension.processor.response.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * 顶级异常
 *
 * @author 单开宇
 * @since 2024-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CentaurException extends RuntimeException {

  private ResultCode resultCode;

  private Object data;

  public CentaurException(@NotNull ResultCode resultCode) {
    this.resultCode = resultCode;
  }

  public CentaurException(@NotNull ResultCode resultCode, Object data) {
    this.resultCode = resultCode;
    this.data = data;
  }
}
