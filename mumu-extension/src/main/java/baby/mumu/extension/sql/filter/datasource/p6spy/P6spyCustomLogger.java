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

import baby.mumu.basis.kotlin.tools.TraceIdUtils;
import com.google.common.base.Strings;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义appender
 * <p>
 * 可以和ELK+logback日志系统集成
 * </p>
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class P6spyCustomLogger extends FormattedLogger {

    private static final Logger log = LoggerFactory.getLogger(P6spyCustomLogger.class);

    /**
     * 定义需要忽略的关键字（表名前缀或特定名称）
     */
    private static final String[] IGNORED_TABLES = {
        "jobrunr_backgroundjobservers",
        "jobrunr_jobs",
        "jobrunr_metadata",
        "jobrunr_migrations",
        "jobrunr_recurring_jobs"
    };

    @Override
    public void logException(Exception e) {
        P6spyCustomLogger.log.info("", e);
    }

    @Override
    public void logText(String text) {
        P6spyCustomLogger.log.info(text);
    }

    @Override
    public void logSQL(int connectionId, String now, long elapsed,
                       Category category, String prepared, String sql, String url) {
        if (!Strings.isNullOrEmpty(sql)) {
            // 1. 精确匹配过滤逻辑
            String lowerSql = sql.toLowerCase();
            for (String tableName : P6spyCustomLogger.IGNORED_TABLES) {
                if (lowerSql.contains(tableName)) {
                    return; // 匹配到具体表名，直接跳过打印
                }
            }
            String lf = "\n";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(lf).append("====>");
            stringBuilder.append("trace-id:[");
            String traceId = TraceIdUtils.getTraceId();
            stringBuilder.append(traceId);
            stringBuilder.append("]");
            String msg = strategy.formatMessage(connectionId, now, elapsed,
                category.toString(), prepared, sql, url);
            stringBuilder.append(msg);

            P6spyCustomLogger.print(category, stringBuilder);
        }
    }

    private static void print(Category category, StringBuilder stringBuilder) {
        if (Category.ERROR.equals(category)) {
            P6spyCustomLogger.log.error(stringBuilder.toString());
        } else if (Category.WARN.equals(category)) {
            P6spyCustomLogger.log.warn(stringBuilder.toString());
        } else if (Category.DEBUG.equals(category)) {
            P6spyCustomLogger.log.debug(stringBuilder.toString());
        } else {
            P6spyCustomLogger.log.info(stringBuilder.toString());
        }
    }


    @Override
    public boolean isCategoryEnabled(Category category) {
        if (Category.ERROR.equals(category)) {
            return P6spyCustomLogger.log.isErrorEnabled();
        } else if (Category.WARN.equals(category)) {
            return P6spyCustomLogger.log.isWarnEnabled();
        } else if (Category.DEBUG.equals(category)) {
            return P6spyCustomLogger.log.isDebugEnabled();
        } else {
            return P6spyCustomLogger.log.isInfoEnabled();
        }
    }

    @Override
    public void setStrategy(MessageFormattingStrategy strategy) {
        this.strategy = new P6spyCustomStrategy();
    }

}
