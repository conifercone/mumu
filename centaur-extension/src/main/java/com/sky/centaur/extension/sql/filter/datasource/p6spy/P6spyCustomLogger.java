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
package com.sky.centaur.extension.sql.filter.datasource.p6spy;

import com.google.common.base.Strings;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.sky.centaur.basis.kotlin.tools.SpringContextUtil;
import de.vandermeer.asciitable.AsciiTable;
import io.micrometer.tracing.Tracer;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 自定义appender
 * <p>
 * 可以和ELK+logback日志系统集成
 * </p>
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public class P6spyCustomLogger extends FormattedLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(P6spyCustomLogger.class);
  public static final Long SQL_EXECUTION_TIME_THRESHOLD = 1000L;
  private static final int MAX_LOG_SIZE = 10;
  private final PriorityQueue<LogEntry> logEntries = new PriorityQueue<>(
      (e1, e2) -> Long.compare(e2.executionTime, e1.executionTime)
  );

  @Override
  public void logException(Exception e) {
    LOGGER.info("", e);
  }

  @Override
  public void logText(String text) {
    LOGGER.info(text);
  }

  @Override
  public void logSQL(int connectionId, String now, long elapsed,
      Category category, String prepared, String sql, String url) {
    if (!Strings.isNullOrEmpty(sql) && !sql.contains("jobrunr_")) {
      if (elapsed >= SQL_EXECUTION_TIME_THRESHOLD) {
        if (logEntries.stream().noneMatch(logEntry -> logEntry.getSql().equals(sql))) {
          logEntries.add(new LogEntry(sql, elapsed));
        }
        if (logEntries.size() > MAX_LOG_SIZE) {
          logEntries.poll();
        }
        logTopSQLs();
      }
      String LF = "\n";
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(LF).append("====>");
      stringBuilder.append("trace-id:[");
      String traceId = Optional.ofNullable(SpringContextUtil.getApplicationContext())
          .flatMap(applicationContext -> Optional.ofNullable(
              applicationContext.getBeanProvider(Tracer.class).getIfAvailable())).flatMap(
              tracer -> Optional.ofNullable(tracer.currentSpan())
                  .map(span -> span.context().traceId())
          ).orElse(null);
      if (Strings.isNullOrEmpty(traceId)) {
        String uuid = String.valueOf(UUID.randomUUID());
        stringBuilder.append(uuid);
        MDC.put("trace-id", uuid);
      } else {
        stringBuilder.append(traceId);
      }
      stringBuilder.append("]");
      String msg = strategy.formatMessage(connectionId, now, elapsed,
          category.toString(), prepared, sql, url);
      stringBuilder.append(msg);

      if (Category.ERROR.equals(category)) {
        LOGGER.error(stringBuilder.toString());
      } else if (Category.WARN.equals(category)) {
        LOGGER.warn(stringBuilder.toString());
      } else if (Category.DEBUG.equals(category)) {
        LOGGER.debug(stringBuilder.toString());
      } else {
        LOGGER.info(stringBuilder.toString());
      }
    }
  }

  @Override
  public boolean isCategoryEnabled(Category category) {
    if (Category.ERROR.equals(category)) {
      return LOGGER.isErrorEnabled();
    } else if (Category.WARN.equals(category)) {
      return LOGGER.isWarnEnabled();
    } else if (Category.DEBUG.equals(category)) {
      return LOGGER.isDebugEnabled();
    } else {
      return LOGGER.isInfoEnabled();
    }
  }

  @Override
  public void setStrategy(MessageFormattingStrategy strategy) {
    this.strategy = new P6spyCustomStrategy();
  }

  @Data
  private static class LogEntry {

    private String sql;
    private long executionTime;

    LogEntry(String sql, long executionTime) {
      this.sql = sql;
      this.executionTime = executionTime;
    }
  }

  private void logTopSQLs() {
    AsciiTable at = new AsciiTable();
    for (LogEntry entry : logEntries) {
      at.addRule();
      String sql = entry.sql.replaceAll("\\r?\\n", "");
      at.addRow(sql);
    }
    at.addRule();
    LOGGER.info("Top SQLs exceeding {} ms:\n{}", SQL_EXECUTION_TIME_THRESHOLD,
        at.render(100));
  }
}
