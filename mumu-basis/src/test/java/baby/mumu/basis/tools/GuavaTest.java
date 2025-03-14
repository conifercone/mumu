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
package baby.mumu.basis.tools;

import com.google.common.reflect.TypeToken;
import java.util.Collection;
import org.junit.jupiter.api.Test;

/**
 * guava测试
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.6.0
 */
public class GuavaTest {

  @Test
  public void typeTokenTest() {
    TypeToken<Collection<String>> collectionTypeToken = new TypeToken<>() {
    };
    System.out.println(collectionTypeToken.getType());
  }
}
