/*
 * Copyright (c) 2024-2026, the original author or authors.
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

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * SQL打印策略
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class P6spyCustomStrategy implements MessageFormattingStrategy {

    // 静态预编译正则
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    public static final String LINE_FEED = StringUtils.LF;
    public static final String TEMPLATE_START = "====>";

    /**
     * 格式化SQL输出
     *
     * @param connectionId the id of the connection
     * @param now          the current ime expressing in milliseconds
     * @param elapsed      the time in milliseconds that the operation took to complete
     * @param category     the category of the operation
     * @param prepared     the SQL statement with all bind variables replaced with actual values
     * @param sql          the SQL statement executed
     * @param url          the database url where the SQL statement executed
     * @return 格式化后的SQL输出
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {
        if (StringUtils.isBlank(sql)) {
            return StringUtils.EMPTY;
        }

        // 预编译的空白字符正则
        // \s+ 表示任意连续空白字符（空格、换行、制表符等）
        // 提前编译避免每次 replaceAll 重新构建 Pattern
        final String normalizedSql = P6spyCustomStrategy.WHITESPACE.matcher(sql)
            .replaceAll(StringUtils.SPACE);

        return P6spyCustomStrategy.LINE_FEED
            + P6spyCustomStrategy.TEMPLATE_START + "time-consuming:[" + elapsed + "]ms"
            + P6spyCustomStrategy.LINE_FEED
            + P6spyCustomStrategy.TEMPLATE_START + "now:[" + now + "]" + P6spyCustomStrategy.LINE_FEED
            + P6spyCustomStrategy.TEMPLATE_START + "sql:[" + normalizedSql + "]"
            + P6spyCustomStrategy.LINE_FEED
            + P6spyCustomStrategy.TEMPLATE_START + "datasource:[" + url + "]"
            + P6spyCustomStrategy.LINE_FEED;
    }
}
