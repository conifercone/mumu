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
package baby.mumu.authentication.client.dto.co;

import baby.mumu.basis.co.BaseClientObject;
import com.opencsv.bean.CsvBindByName;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下载所有权限数据客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionDownloadAllCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 6242302479488819166L;

  @CsvBindByName(column = "Id")
  private Long id;

  @CsvBindByName(column = "Code")
  private String code;

  @CsvBindByName(column = "Name")
  private String name;

  @CsvBindByName(column = "Description")
  private String description;
}