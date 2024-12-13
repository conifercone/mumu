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

import baby.mumu.authentication.client.cmds.PermissionUpdateCmd;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.infrastructure.permission.convertor.PermissionMapper;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.dataobject.PermissionDo;
import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

/**
 * AuthorityMapper单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
public class PermissionMapperTest {

  @Test
  public void toEntity() {
    PermissionMapper instance = PermissionMapper.INSTANCE;
    PermissionDo permissionDo = new PermissionDo();
    permissionDo.setId(1L);
    permissionDo.setCode("test");
    permissionDo.setModifier(1L);
    permissionDo.setModificationTime(OffsetDateTime.now());
    Permission entity = instance.toEntity(permissionDo);
    System.out.println(entity);
  }

  @Test
  public void toEntityForExistObject() {
    PermissionMapper instance = PermissionMapper.INSTANCE;
    PermissionUpdateCmd permissionUpdateCmd = new PermissionUpdateCmd();
    permissionUpdateCmd.setId(1L);
    permissionUpdateCmd.setCode("test");
    Permission permission = new Permission();
    permission.setId(2L);
    permission.setName("test");
    instance.toEntity(permissionUpdateCmd, permission);
    System.out.println(permission);
  }

  @Test
  public void offsetDateTime2Timestamp() {
    OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"));
    PermissionMapper instance = PermissionMapper.INSTANCE;
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
    PermissionMapper instance = PermissionMapper.INSTANCE;
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
    PermissionMapper instance = PermissionMapper.INSTANCE;
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
  public void timestamp2OffsetDateTime() {
    Timestamp timestamp = Timestamp.newBuilder()
      .setSeconds(1698643200)
      .setNanos(0)
      .build();
    PermissionMapper instance = PermissionMapper.INSTANCE;
    OffsetDateTime offsetDateTime = instance.toOffsetDateTime(timestamp);
    String format = offsetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
    System.out.println(format);
  }

  @Test
  public void timestamp2LocalDateTime() {
    Timestamp timestamp = Timestamp.newBuilder()
      .setSeconds(1698643200)
      .setNanos(0)
      .build();
    PermissionMapper instance = PermissionMapper.INSTANCE;
    LocalDateTime localDateTime = instance.toLocalDateTime(timestamp);
    String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    System.out.println(format);
  }

  @Test
  public void timestamp2LocalDate() {
    Timestamp timestamp = Timestamp.newBuilder()
      .setSeconds(1698643200)
      .setNanos(0)
      .build();
    PermissionMapper instance = PermissionMapper.INSTANCE;
    LocalDate localDate = instance.toLocalDate(timestamp);
    String format = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    System.out.println(format);
  }
}
