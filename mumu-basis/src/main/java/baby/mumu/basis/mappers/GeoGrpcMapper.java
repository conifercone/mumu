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

import com.google.type.LatLng;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * grpc地理坐标相关转换
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.6.0
 */
public interface GeoGrpcMapper {

  @API(status = Status.STABLE, since = "2.6.0")
  default LatLng map(GeoJsonPoint geoJsonPoint) {
    return Optional.ofNullable(geoJsonPoint).map(
      geoJsonPointNotNull -> LatLng.newBuilder().setLongitude(geoJsonPointNotNull.getX())
        .setLatitude(geoJsonPointNotNull.getY())
        .build()).orElse(null);
  }

}
