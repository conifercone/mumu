/*
 * Copyright (c) 2024-2025, the original author or authors.
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

import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

/**
 * P6Factory
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class MuMuSqlLogFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6LogOptions(optionsRepository);
  }

  @Override
  public JdbcEventListener getJdbcEventListener() {
    return MuMuSqlLoggingEventListener.getInstance();
  }
}

