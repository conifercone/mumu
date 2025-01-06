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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * base mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
public interface BaseMapper {

  default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
    return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
  }

  default GeoJsonPoint toGeoJsonPoint(Point point) {
    if (point == null) {
      return null;
    }
    return new GeoJsonPoint(point);
  }

  default Point toPoint(GeoJsonPoint geoJsonPoint) {
    if (geoJsonPoint == null) {
      return null;
    }
    return new Point(geoJsonPoint);
  }
}
