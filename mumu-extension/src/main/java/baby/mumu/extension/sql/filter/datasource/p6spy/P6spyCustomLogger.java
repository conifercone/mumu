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

import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.kotlin.tools.SpringContextUtil;
import com.google.common.base.Strings;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import de.vandermeer.asciitable.AsciiTable;
import io.micrometer.tracing.Tracer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 自定义appender
 * <p>
 * 可以和ELK+logback日志系统集成
 * </p>
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class P6spyCustomLogger extends FormattedLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(P6spyCustomLogger.class);
  public static final Long SQL_EXECUTION_TIME_THRESHOLD = 1000L;
  private static final int MAX_LOG_SIZE = 10;
  private final ConcurrentSkipListMap<Long, String> slowQueries = new ConcurrentSkipListMap<>(
    (a, b) -> Long.compare(b, a));

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
      slowSqlRecord(elapsed, sql);
      String lf = "\n";
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(lf).append("====>");
      stringBuilder.append("trace-id:[");
      String traceId = null;
      try {
        traceId = Optional.ofNullable(SpringContextUtil.getApplicationContext())
          .flatMap(applicationContext -> Optional.ofNullable(
            applicationContext.getBeanProvider(Tracer.class).getIfAvailable())).flatMap(
            tracer -> Optional.ofNullable(tracer.currentSpan())
              .map(span -> span.context().traceId())
          ).orElse(null);
      } catch (Exception e) {
        // ignore
      }
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

      print(category, stringBuilder);
    }
  }

  private static void print(Category category, StringBuilder stringBuilder) {
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

  private void slowSqlRecord(long elapsed, String sql) {
    if (elapsed >= SQL_EXECUTION_TIME_THRESHOLD) {
      if (!slowQueries.containsValue(sql)) {
        slowQueries.put(elapsed, sql);
      }
      if (slowQueries.size() > MAX_LOG_SIZE) {
        slowQueries.pollLastEntry();
      }
      logTopSQLs();
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

  private void logTopSQLs() {
    AsciiTable at = new AsciiTable();
    at.addRule();
    at.addRow("", "", "", "", "", "", "", "");
    at.addRule();
    at.addRow("Execution time", null, null, null, null, null, null, "SQL");
    for (Map.Entry<Long, String> entry : slowQueries.entrySet()) {
      at.addRule();
      String sql = entry.getValue().replaceAll("\\r?\\n", "");
      at.addRow(entry.getKey().toString().concat("ms"), null, null, null, null, null, null, sql);
    }
    at.addRule();
    Collection<String> renderAsCollection = at.renderAsCollection();
    LinkedList<String> strings = new LinkedList<>(renderAsCollection);
    strings.removeFirst();
    strings.addFirst(CommonConstants.SLOW_SQL_TABLE_HEADER);
    strings.remove(1);
    strings.remove(1);
    LOGGER.info("Top SQLs exceeding {} ms:\n{}", SQL_EXECUTION_TIME_THRESHOLD,
      String.join("\r\n", strings));
  }
}
