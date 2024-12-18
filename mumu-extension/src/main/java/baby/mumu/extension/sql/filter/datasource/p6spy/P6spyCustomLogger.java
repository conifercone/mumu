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

import baby.mumu.basis.kotlin.tools.SpringContextUtil;
import com.google.common.base.Strings;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import io.micrometer.tracing.Tracer;
import java.util.Optional;
import java.util.UUID;
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

  private static final Logger logger = LoggerFactory.getLogger(P6spyCustomLogger.class);

  @Override
  public void logException(Exception e) {
    logger.info("", e);
  }

  @Override
  public void logText(String text) {
    logger.info(text);
  }

  @Override
  public void logSQL(int connectionId, String now, long elapsed,
    Category category, String prepared, String sql, String url) {
    if (!Strings.isNullOrEmpty(sql) && !sql.contains("jobrunr_")) {
      String lf = "\n";
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(lf).append("====>");
      stringBuilder.append("trace-id:[");
      String traceId;
      try {
        traceId = Optional.ofNullable(SpringContextUtil.getApplicationContext())
          .flatMap(applicationContext -> Optional.ofNullable(
            applicationContext.getBeanProvider(Tracer.class).getIfAvailable())).flatMap(
            tracer -> Optional.ofNullable(tracer.currentSpan())
              .map(span -> span.context().traceId())
          ).orElse(null);
      } catch (Exception e) {
        traceId = "";
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
      logger.error(stringBuilder.toString());
    } else if (Category.WARN.equals(category)) {
      logger.warn(stringBuilder.toString());
    } else if (Category.DEBUG.equals(category)) {
      logger.debug(stringBuilder.toString());
    } else {
      logger.info(stringBuilder.toString());
    }
  }


  @Override
  public boolean isCategoryEnabled(Category category) {
    if (Category.ERROR.equals(category)) {
      return logger.isErrorEnabled();
    } else if (Category.WARN.equals(category)) {
      return logger.isWarnEnabled();
    } else if (Category.DEBUG.equals(category)) {
      return logger.isDebugEnabled();
    } else {
      return logger.isInfoEnabled();
    }
  }

  @Override
  public void setStrategy(MessageFormattingStrategy strategy) {
    this.strategy = new P6spyCustomStrategy();
  }

}
