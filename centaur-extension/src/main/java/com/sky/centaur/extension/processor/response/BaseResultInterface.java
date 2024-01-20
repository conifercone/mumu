package com.sky.centaur.extension.processor.response;

/**
 * 基础响应顶级接口
 *
 * @author 单开宇
 * @since 2024-01-20
 */
public interface BaseResultInterface {

  /**
   * 响应码
   *
   * @return 响应码
   */
  String getResultCode();

  /**
   * 错误描述
   *
   * @return 错误描述
   */
  String getResultMsg();
}
