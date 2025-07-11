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

package baby.mumu.log.infra.system.gatewayimpl.elasticsearch.po;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.log.infra.config.LogProperties;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 * 系统日志es数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Document(indexName = LogProperties.SYSTEM_LOG_ES_INDEX_NAME)
@Data
@Metamodel
public class SystemLogEsPO {

  /**
   * 唯一标识
   */
  @Id
  private String id;

  /**
   * 日志内容
   */
  @MultiField(
    mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
    otherFields = {
      @InnerField(suffix = CommonConstants.ES_MAPPING_EN_SUFFIX, type = FieldType.Text, analyzer = "english", searchAnalyzer = "english"),
      @InnerField(suffix = CommonConstants.ES_MAPPING_SP_SUFFIX, type = FieldType.Text, analyzer = "simple", searchAnalyzer = "simple")
    }
  )
  private String content;

  /**
   * 系统日志的种类
   */
  @Field(type = FieldType.Keyword)
  private String category;

  /**
   * 系统日志成功的文本模板
   */
  @MultiField(
    mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
    otherFields = {
      @InnerField(suffix = CommonConstants.ES_MAPPING_EN_SUFFIX, type = FieldType.Text, analyzer = "english", searchAnalyzer = "english"),
      @InnerField(suffix = CommonConstants.ES_MAPPING_SP_SUFFIX, type = FieldType.Text, analyzer = "simple", searchAnalyzer = "simple")
    }
  )
  private String success;

  /**
   * 系统日志失败的文本模板
   */
  @MultiField(
    mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
    otherFields = {
      @InnerField(suffix = CommonConstants.ES_MAPPING_EN_SUFFIX, type = FieldType.Text, analyzer = "english", searchAnalyzer = "english"),
      @InnerField(suffix = CommonConstants.ES_MAPPING_SP_SUFFIX, type = FieldType.Text, analyzer = "simple", searchAnalyzer = "simple")
    }
  )
  private String fail;

  /**
   * 系统日志的记录时间
   */
  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
  private LocalDateTime recordTime;
}
