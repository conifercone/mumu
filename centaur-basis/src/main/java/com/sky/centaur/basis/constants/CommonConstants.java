package com.sky.centaur.basis.constants;

/**
 * 常用常量类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class CommonConstants {

  /**
   * 百分号
   */
  public static final String PERCENT_SIGN = "%";

  /**
   * string格式化占位符
   */
  public static final String STRING_FORMAT = "%s";

  /**
   * sql 左右模糊查询模板
   */
  public static final String LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE = PERCENT_SIGN.concat(PERCENT_SIGN)
      .concat(
          STRING_FORMAT).concat(PERCENT_SIGN).concat(PERCENT_SIGN);
}
