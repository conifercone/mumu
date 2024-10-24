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
package baby.mumu.log.infrastructure.system.gatewayimpl;

import static baby.mumu.basis.constants.CommonConstants.ES_QUERY_EN;
import static baby.mumu.basis.constants.CommonConstants.ES_QUERY_SP;

import baby.mumu.basis.exception.DataConversionException;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import baby.mumu.log.domain.system.SystemLog;
import baby.mumu.log.domain.system.gateway.SystemLogGateway;
import baby.mumu.log.infrastructure.config.LogProperties;
import baby.mumu.log.infrastructure.system.convertor.SystemLogConvertor;
import baby.mumu.log.infrastructure.system.gatewayimpl.elasticsearch.SystemLogEsRepository;
import baby.mumu.log.infrastructure.system.gatewayimpl.elasticsearch.dataobject.SystemLogEsDo;
import baby.mumu.log.infrastructure.system.gatewayimpl.elasticsearch.dataobject.SystemLogEsDoMetamodel;
import baby.mumu.log.infrastructure.system.gatewayimpl.kafka.SystemLogKafkaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * 系统日志领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class SystemLogGatewayImpl implements SystemLogGateway {

  private final SystemLogKafkaRepository systemLogKafkaRepository;
  private final SystemLogEsRepository systemLogEsRepository;
  private final ObjectMapper objectMapper;
  private final ElasticsearchTemplate elasticsearchTemplate;
  private final SystemLogConvertor systemLogConvertor;

  @Autowired
  public SystemLogGatewayImpl(SystemLogKafkaRepository systemLogKafkaRepository,
      SystemLogEsRepository systemLogEsRepository, ObjectMapper objectMapper,
      ElasticsearchTemplate elasticsearchTemplate, SystemLogConvertor systemLogConvertor) {
    this.systemLogKafkaRepository = systemLogKafkaRepository;
    this.systemLogEsRepository = systemLogEsRepository;
    this.objectMapper = objectMapper;
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.systemLogConvertor = systemLogConvertor;
  }

  @Override
  public void submit(SystemLog systemLog) {
    systemLogConvertor.toKafkaDataObject(systemLog).ifPresent(res -> {
      try {
        systemLogKafkaRepository.send(LogProperties.SYSTEM_LOG_KAFKA_TOPIC_NAME,
            objectMapper.writeValueAsString(res));
      } catch (JsonProcessingException e) {
        throw new DataConversionException();
      }
    });
  }

  @Override
  public void save(SystemLog systemLog) {
    systemLogConvertor.toEsDataObject(systemLog).ifPresent(systemLogEsRepository::save);
  }

  @Override
  @SuppressWarnings("DuplicatedCode")
  public Page<SystemLog> findAll(SystemLog systemLog, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Criteria criteria = new Criteria();
    Optional.ofNullable(systemLog).ifPresent(sysLog -> {
      Optional.ofNullable(sysLog.getId())
          .ifPresent(id -> criteria.and(
              new Criteria(SystemLogEsDoMetamodel.id).matches(id)));
      Optional.ofNullable(sysLog.getContent())
          .ifPresent(content -> {
            String propertyName = SystemLogEsDoMetamodel.content;
            criteria.and(
                new Criteria(propertyName).matches(content).or(propertyName.concat(ES_QUERY_EN))
                    .matches(content).or(propertyName.concat(ES_QUERY_SP))
                    .matches(content));
          });
      Optional.ofNullable(sysLog.getCategory())
          .ifPresent(category -> criteria.and(
              new Criteria(SystemLogEsDoMetamodel.category).matches(
                  category)));
      Optional.ofNullable(sysLog.getSuccess())
          .ifPresent(success -> {
            String propertyName = SystemLogEsDoMetamodel.success;
            criteria.and(
                new Criteria(propertyName).matches(success).or(propertyName.concat(ES_QUERY_EN))
                    .matches(success).or(propertyName.concat(ES_QUERY_SP))
                    .matches(success));
          });
      Optional.ofNullable(sysLog.getFail())
          .ifPresent(fail -> {
            String propertyName = SystemLogEsDoMetamodel.fail;
            criteria.and(
                new Criteria(propertyName).matches(fail).or(propertyName.concat(ES_QUERY_SP))
                    .matches(fail));
          });
      Optional.ofNullable(sysLog.getRecordTime())
          .ifPresent(
              recordTime -> criteria.and(new Criteria(
                  SystemLogEsDoMetamodel.recordTime).matches(
                  CommonUtil.convertAccountZoneToUTC(recordTime))));
      Optional.ofNullable(sysLog.getRecordStartTime())
          .ifPresent(
              recordStartTime -> criteria.and(
                  new Criteria(
                      SystemLogEsDoMetamodel.recordTime).greaterThan(
                      CommonUtil.convertAccountZoneToUTC(recordStartTime))));
      Optional.ofNullable(sysLog.getRecordEndTime())
          .ifPresent(
              recordEndTime -> criteria.and(
                  new Criteria(
                      SystemLogEsDoMetamodel.recordTime).lessThan(
                      CommonUtil.convertAccountZoneToUTC(recordEndTime))));
    });
    Query query = new CriteriaQuery(criteria).setPageable(pageRequest)
        .addSort(
            Sort.by(SystemLogEsDoMetamodel.recordTime).descending());
    SearchHits<SystemLogEsDo> searchHits = elasticsearchTemplate.search(query,
        SystemLogEsDo.class);
    List<SystemLog> systemLogs = searchHits.getSearchHits().stream()
        .map(SearchHit::getContent).map(systemLogConvertor::toEntity)
        .filter(Optional::isPresent).map(Optional::get)
        .peek(systemLogDomain -> systemLogDomain.setRecordTime(
            CommonUtil.convertUTCToAccountZone(systemLogDomain.getRecordTime())))
        .toList();
    return new PageImpl<>(systemLogs, pageRequest, searchHits.getTotalHits());

  }
}
