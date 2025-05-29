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
package baby.mumu.basis.mappers;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * grpc mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
public interface GrpcMapper {

  @API(status = Status.STABLE, since = "2.2.0")
  default Int64Value map(Long value) {
    return Int64Value.of(value);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  default Int32Value map(Integer value) {
    return Int32Value.of(value);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  default Long map(Int64Value value) {
    return Optional.ofNullable(value).filter(Int64Value::isInitialized).map(Int64Value::getValue)
      .orElse(null);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  default Integer map(Int32Value value) {
    return Optional.ofNullable(value).filter(Int32Value::isInitialized).map(Int32Value::getValue)
      .orElse(null);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  default BoolValue map(Boolean value) {
    return BoolValue.of(value);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  default StringValue map(String value) {
    return StringValue.of(value);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  default String map(StringValue value) {
    return Optional.ofNullable(value).filter(StringValue::isInitialized).map(StringValue::getValue)
      .orElse(null);
  }

  @API(status = Status.STABLE, since = "2.3.0")
  default Timestamp map(OffsetDateTime offsetDateTime) {
    return Optional.ofNullable(offsetDateTime).map(
      _ -> Timestamp.newBuilder().setSeconds(offsetDateTime.toEpochSecond())
        .setNanos(offsetDateTime.getNano()).build()).orElse(Timestamp.getDefaultInstance());
  }

  @API(status = Status.STABLE, since = "2.3.0")
  default Timestamp map(LocalDate localDate) {
    return Optional.ofNullable(localDate).map(
        localDateNotNull -> Timestamp.newBuilder()
          .setSeconds(localDateNotNull.atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond())
          .setNanos(localDateNotNull.atStartOfDay().toInstant(ZoneOffset.UTC).getNano()).build())
      .orElse(Timestamp.getDefaultInstance());
  }

  @API(status = Status.STABLE, since = "2.3.0")
  default Timestamp map(LocalDateTime localDateTime) {
    return Optional.ofNullable(localDateTime).map(
        localDateTimeNotNull -> Timestamp.newBuilder()
          .setSeconds(localDateTimeNotNull.toInstant(ZoneOffset.UTC).getEpochSecond())
          .setNanos(localDateTimeNotNull.toInstant(ZoneOffset.UTC).getNano()).build())
      .orElse(Timestamp.getDefaultInstance());
  }

  @API(status = Status.STABLE, since = "2.3.0")
  default OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
    return Optional.ofNullable(timestamp).map(
        timestampNotNull -> Instant.ofEpochSecond(timestampNotNull.getSeconds(),
          timestampNotNull.getNanos()).atZone(ZoneOffset.UTC).toOffsetDateTime())
      .orElse(null);
  }

  @API(status = Status.STABLE, since = "2.3.0")
  default LocalDate toLocalDate(Timestamp timestamp) {
    return Optional.ofNullable(timestamp).map(
        timestampNotNull -> Instant.ofEpochSecond(timestampNotNull.getSeconds(),
          timestampNotNull.getNanos()).atZone(ZoneOffset.UTC).toLocalDate())
      .orElse(null);
  }

  @API(status = Status.STABLE, since = "2.3.0")
  default LocalDateTime toLocalDateTime(Timestamp timestamp) {
    return Optional.ofNullable(timestamp).map(
        timestampNotNull -> Instant.ofEpochSecond(timestampNotNull.getSeconds(),
          timestampNotNull.getNanos()).atZone(ZoneOffset.UTC).toLocalDateTime())
      .orElse(null);
  }

}
