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
package baby.mumu.authentication.mapstruct;

import baby.mumu.authentication.client.dto.co.AuthorityUpdateCo;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityMapper;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/**
 * AuthorityMapper单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
public class AuthorityMapperTest {

  @Test
  public void toEntity() {
    AuthorityMapper instance = AuthorityMapper.INSTANCE;
    AuthorityDo authorityDo = new AuthorityDo();
    authorityDo.setId(1L);
    authorityDo.setCode("test");
    authorityDo.setModifier(1L);
    authorityDo.setModificationTime(OffsetDateTime.now());
    Authority entity = instance.toEntity(authorityDo);
    System.out.println(entity);
  }

  @Test
  public void toEntityForExistObject() {
    AuthorityMapper instance = AuthorityMapper.INSTANCE;
    AuthorityUpdateCo authorityUpdateCo = new AuthorityUpdateCo();
    authorityUpdateCo.setId(1L);
    authorityUpdateCo.setCode("test");
    Authority authority = new Authority();
    authority.setId(2L);
    authority.setName("test");
    instance.toEntity(authorityUpdateCo, authority);
    System.out.println(authority);
  }

  @Test
  public void offsetDateTime2Timestamp() {
    OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"));
    AuthorityMapper instance = AuthorityMapper.INSTANCE;
    Timestamp timestamp = instance.map(offsetDateTime);
    // 从 Timestamp 获取秒和纳秒
    long seconds = timestamp.getSeconds();
    int nanos = timestamp.getNanos();
    // 将 Timestamp 转换为 Instant
    Instant instant = Instant.ofEpochSecond(seconds, nanos);
    // 将 Instant 转换为 LocalDate
    LocalDate localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate();
    // 获取年、月、日
    int year = localDate.getYear();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();
    // 打印结果
    System.out.println("Year: " + year);
    System.out.println("Month: " + month);
    System.out.println("Day: " + day);
    System.out.println(timestamp);
  }

  @Test
  public void localDate2Timestamp() {
    LocalDate now = LocalDate.now(ZoneId.of("UTC"));
    AuthorityMapper instance = AuthorityMapper.INSTANCE;
    Timestamp timestamp = instance.map(now);
    // 从 Timestamp 获取秒和纳秒
    long seconds = timestamp.getSeconds();
    int nanos = timestamp.getNanos();
    // 将 Timestamp 转换为 Instant
    Instant instant = Instant.ofEpochSecond(seconds, nanos);
    // 将 Instant 转换为 LocalDate
    LocalDate localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate();
    // 获取年、月、日
    int year = localDate.getYear();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();
    // 打印结果
    System.out.println("Year: " + year);
    System.out.println("Month: " + month);
    System.out.println("Day: " + day);
    System.out.println(timestamp);
  }

  @Test
  public void localDateTime2Timestamp() {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
    AuthorityMapper instance = AuthorityMapper.INSTANCE;
    Timestamp timestamp = instance.map(now);
    // 从 Timestamp 获取秒和纳秒
    long seconds = timestamp.getSeconds();
    int nanos = timestamp.getNanos();
    // 将 Timestamp 转换为 Instant
    Instant instant = Instant.ofEpochSecond(seconds, nanos);
    // 将 Instant 转换为 LocalDate
    LocalDate localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate();
    // 获取年、月、日
    int year = localDate.getYear();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();
    // 打印结果
    System.out.println("Year: " + year);
    System.out.println("Month: " + month);
    System.out.println("Day: " + day);
    System.out.println(timestamp);
  }
}
