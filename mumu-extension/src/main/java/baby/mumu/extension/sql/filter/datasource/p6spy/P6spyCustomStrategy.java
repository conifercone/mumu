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
package baby.mumu.extension.sql.filter.datasource.p6spy;

import com.google.common.base.Strings;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * SQL打印策略
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class P6spyCustomStrategy implements MessageFormattingStrategy {

  /**
   * 格式化SQL输出
   *
   * @param connectionId the id of the connection
   * @param now          the current ime expressing in milliseconds
   * @param elapsed      the time in milliseconds that the operation took to complete
   * @param category     the category of the operation
   * @param prepared     the SQL statement with all bind variables replaced with actual values
   * @param sql          the sql statement executed
   * @param url          the database url where the sql statement executed
   * @return 格式化后的SQL输出
   */
  @Override
  public String formatMessage(int connectionId, String now, long elapsed, String category,
    String prepared, String sql, String url) {
    String templateStart = "====>";
    String templateEnd = "\n";
    //耗时格式化模板
    String timeConsumingFormatTemplate = templateStart + "time-consuming:[%s]ms" + templateEnd;
    //sql格式化模板
    String sqlFormatTemplate = templateStart + "sql:[%s]" + templateEnd;
    //数据源信息格式化模板
    String datasourceFormatTemplate = templateStart + "datasource:[%s]" + templateEnd;
    //当前时间
    String nowFormatTemplate = templateStart + "now:[%s]" + templateEnd;
    return !Strings.isNullOrEmpty(sql) ? templateEnd + String.format(timeConsumingFormatTemplate,
      elapsed) + String.format(nowFormatTemplate,
      now) + String.format(sqlFormatTemplate, sql.replaceAll("\\s+", " ")) + String.format(
      datasourceFormatTemplate, url) : "";
  }
}
