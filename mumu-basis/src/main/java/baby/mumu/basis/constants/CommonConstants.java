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
package baby.mumu.basis.constants;

/**
 * 常用常量类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public final class CommonConstants {

  private CommonConstants() {
  }

  /**
   * 百分号
   */
  public static final String PERCENT_SIGN = "%";

  /**
   * string格式化占位符
   */
  public static final String STRING_FORMAT = "%s";

  /**
   * es 英文映射前缀
   */
  public static final String ES_MAPPING_EN_SUFFIX = "en";

  /**
   * es simple映射前缀
   */
  public static final String ES_MAPPING_SP_SUFFIX = "sp";

  /**
   * dot
   */
  public static final String DOT = ".";

  /**
   * es 查询英文映射
   */
  public static final String ES_QUERY_EN = DOT + ES_MAPPING_EN_SUFFIX;

  /**
   * es 查询simple映射
   */
  public static final String ES_QUERY_SP = DOT + ES_MAPPING_SP_SUFFIX;

  /**
   * sql 左右模糊查询模板
   */
  public static final String LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE = PERCENT_SIGN.concat(PERCENT_SIGN)
      .concat(
          STRING_FORMAT).concat(PERCENT_SIGN).concat(PERCENT_SIGN);

  /**
   * 角色前缀
   */
  public static final String ROLE_PREFIX = "ROLE_";

  /**
   * 权限前缀
   */
  public static final String AUTHORITY_PREFIX = "SCOPE_";

  /**
   * data url模板
   */
  public static final String DATA_URL_TEMPLATE = "data:%s;base64,%s";

  /**
   * 慢sql表头
   */
  public static final String SLOW_SQL_TABLE_HEADER = "┌─────────┬────────────────────────────────────────────────────────────────────┐";
}
