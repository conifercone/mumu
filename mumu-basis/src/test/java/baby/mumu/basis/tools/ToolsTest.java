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
package baby.mumu.basis.tools;

import baby.mumu.basis.kotlin.tools.CommonUtil;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 工具类单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
public class ToolsTest {

  @Test
  public void generateRandomString() {
    String randomString = CommonUtil.generateRandomString(4);
    Assertions.assertNotNull(randomString);
  }

  @Test
  public void getAvailableZoneIds() {
    Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
    availableZoneIds.forEach(System.out::println);
  }

  @Test
  public void getCountriesInEnglish() {
    List<String> collect = Arrays.stream(Locale.getISOCountries())
        .map(code -> Locale.of("", code))
        .map(res -> res.getDisplayCountry(Locale.ENGLISH))
        .toList();
    System.out.println(collect);
  }

  @Test
  public void isNoneBlank() {
    System.out.println((String) null);
    System.out.println((String[]) null);
    Assertions.assertFalse(StringUtils.isNoneBlank((String) null));
    Assertions.assertTrue(StringUtils.isNoneBlank((String[]) null));
    Assertions.assertFalse(StringUtils.isNoneBlank(null, "foo"));
    Assertions.assertFalse(StringUtils.isNoneBlank(null, null));
    Assertions.assertFalse(StringUtils.isNoneBlank("", "bar"));
    Assertions.assertFalse(StringUtils.isNoneBlank("bob", ""));
    Assertions.assertFalse(StringUtils.isNoneBlank("  bob  ", null));
    Assertions.assertFalse(StringUtils.isNoneBlank(" ", "bar"));
    Assertions.assertTrue(StringUtils.isNoneBlank(new String[]{}));
    Assertions.assertFalse(StringUtils.isNoneBlank(new String[]{""}));
    Assertions.assertTrue(StringUtils.isNoneBlank("foo", "bar"));
  }

  @Test
  public void isNotBlank() {
    System.out.println((String) null);
    //noinspection RedundantCast
    Assertions.assertFalse(StringUtils.isNotBlank((String) null));
  }
}
