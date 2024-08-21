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
package com.sky.centaur.log.infrastructure.operation.gatewayimpl.elasticsearch.dataobject;

import com.sky.centaur.basis.annotations.GenerateDescription;
import com.sky.centaur.basis.constants.CommonConstants;
import com.sky.centaur.log.infrastructure.config.LogProperties;
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
 * 操作日志es数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Document(indexName = LogProperties.OPERATION_LOG_ES_INDEX_NAME)
@Data
@GenerateDescription
public class OperationLogEsDo {

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
   * 操作日志的执行人
   */
  @MultiField(
      mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
      otherFields = {
          @InnerField(suffix = CommonConstants.ES_MAPPING_EN_SUFFIX, type = FieldType.Text, analyzer = "english", searchAnalyzer = "english"),
          @InnerField(suffix = CommonConstants.ES_MAPPING_SP_SUFFIX, type = FieldType.Text, analyzer = "simple", searchAnalyzer = "simple")
      }
  )
  private String operator;

  /**
   * 操作日志绑定的业务对象标识
   */
  @Field(type = FieldType.Keyword)
  private String bizNo;

  /**
   * 操作日志的种类
   */
  @Field(type = FieldType.Keyword)
  private String category;

  /**
   * 扩展参数，记录操作日志的修改详情
   */
  @MultiField(
      mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
      otherFields = {
          @InnerField(suffix = CommonConstants.ES_MAPPING_EN_SUFFIX, type = FieldType.Text, analyzer = "english", searchAnalyzer = "english"),
          @InnerField(suffix = CommonConstants.ES_MAPPING_SP_SUFFIX, type = FieldType.Text, analyzer = "simple", searchAnalyzer = "simple")
      }
  )
  private String detail;

  /**
   * 操作日志成功的文本模板
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
   * 操作日志失败的文本模板
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
   * 操作日志的操作时间
   */
  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
  private LocalDateTime operatingTime;
}
