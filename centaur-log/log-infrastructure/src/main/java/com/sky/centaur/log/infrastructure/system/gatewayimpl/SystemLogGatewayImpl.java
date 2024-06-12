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
package com.sky.centaur.log.infrastructure.system.gatewayimpl;

import static com.sky.centaur.basis.constants.CommonConstants.ES_QUERY_EN;
import static com.sky.centaur.basis.constants.CommonConstants.ES_QUERY_SP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.basis.exception.DataConversionException;
import com.sky.centaur.basis.kotlin.tools.BeanUtil;
import com.sky.centaur.basis.kotlin.tools.CommonUtil;
import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
import com.sky.centaur.log.domain.system.SystemLog;
import com.sky.centaur.log.domain.system.gateway.SystemLogGateway;
import com.sky.centaur.log.infrastructure.system.convertor.SystemLogConvertor;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.elasticsearch.SystemLogEsRepository;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.elasticsearch.dataobject.SystemLogEsDo;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.kafka.SystemLogKafkaRepository;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
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
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class SystemLogGatewayImpl implements SystemLogGateway {

  private final SystemLogKafkaRepository systemLogKafkaRepository;

  private final SystemLogEsRepository systemLogEsRepository;

  private final ObjectMapper objectMapper;

  private final ElasticsearchTemplate elasticsearchTemplate;

  @Autowired
  public SystemLogGatewayImpl(SystemLogKafkaRepository systemLogKafkaRepository,
      SystemLogEsRepository systemLogEsRepository, ObjectMapper objectMapper,
      ElasticsearchTemplate elasticsearchTemplate) {
    this.systemLogKafkaRepository = systemLogKafkaRepository;
    this.systemLogEsRepository = systemLogEsRepository;
    this.objectMapper = objectMapper;
    this.elasticsearchTemplate = elasticsearchTemplate;
  }

  @Override
  public void submit(SystemLog systemLog) {
    SystemLogConvertor.toKafkaDataObject(systemLog).ifPresent(res -> {
      try {
        systemLogKafkaRepository.send("system-log", objectMapper.writeValueAsString(res));
      } catch (JsonProcessingException e) {
        throw new DataConversionException();
      }
    });
  }

  @Override
  public void save(SystemLog systemLog) {
    SystemLogConvertor.toEsDataObject(systemLog).ifPresent(systemLogEsRepository::save);
  }

  @Override
  @SuppressWarnings("DuplicatedCode")
  public Page<SystemLog> findAll(SystemLog systemLog, int pageNo, int pageSize) {
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Criteria criteria = new Criteria();
    Optional.ofNullable(systemLog).ifPresent(sysLog -> {
      Optional.ofNullable(sysLog.getId())
          .ifPresent(id -> criteria.and(
              new Criteria(BeanUtil.getPropertyName(SystemLogEsDo::getId)).matches(id)));
      Optional.ofNullable(sysLog.getContent())
          .ifPresent(content -> {
            String propertyName = BeanUtil.getPropertyName(SystemLogEsDo::getContent);
            criteria.and(
                new Criteria(propertyName).matches(content).or(propertyName.concat(ES_QUERY_EN))
                    .matches(content).or(propertyName.concat(ES_QUERY_SP))
                    .matches(content));
          });
      Optional.ofNullable(sysLog.getCategory())
          .ifPresent(category -> criteria.and(
              new Criteria(BeanUtil.getPropertyName(SystemLogEsDo::getCategory)).matches(
                  category)));
      Optional.ofNullable(sysLog.getSuccess())
          .ifPresent(success -> {
            String propertyName = BeanUtil.getPropertyName(SystemLogEsDo::getSuccess);
            criteria.and(
                new Criteria(propertyName).matches(success).or(propertyName.concat(ES_QUERY_EN))
                    .matches(success).or(propertyName.concat(ES_QUERY_SP))
                    .matches(success));
          });
      Optional.ofNullable(sysLog.getFail())
          .ifPresent(fail -> {
            String propertyName = BeanUtil.getPropertyName(SystemLogEsDo::getFail);
            criteria.and(
                new Criteria(propertyName).matches(fail).or(propertyName.concat(ES_QUERY_SP))
                    .matches(fail));
          });
      Optional.ofNullable(sysLog.getRecordTime())
          .ifPresent(
              recordTime -> criteria.and(new Criteria(
                  BeanUtil.getPropertyName(SystemLogEsDo::getRecordTime)).matches(
                  SecurityContextUtil.getLoginAccountTimezone()
                      .map(timezone -> CommonUtil.convertToUTC(recordTime,
                          ZoneId.of(timezone)))
                      .orElse(recordTime))));
      Optional.ofNullable(sysLog.getRecordStartTime())
          .ifPresent(
              recordStartTime -> criteria.and(
                  new Criteria(
                      BeanUtil.getPropertyName(SystemLogEsDo::getRecordTime)).greaterThan(
                      SecurityContextUtil.getLoginAccountTimezone()
                          .map(timezone -> CommonUtil.convertToUTC(recordStartTime,
                              ZoneId.of(timezone)))
                          .orElse(recordStartTime))));
      Optional.ofNullable(sysLog.getRecordEndTime())
          .ifPresent(
              recordEndTime -> criteria.and(
                  new Criteria(
                      BeanUtil.getPropertyName(SystemLogEsDo::getRecordTime)).lessThan(
                      SecurityContextUtil.getLoginAccountTimezone()
                          .map(timezone -> CommonUtil.convertToUTC(recordEndTime,
                              ZoneId.of(timezone)))
                          .orElse(recordEndTime))));
    });
    Query query = new CriteriaQuery(criteria).setPageable(pageRequest)
        .addSort(
            Sort.by(BeanUtil.getPropertyName(SystemLogEsDo::getRecordTime)).descending());
    SearchHits<SystemLogEsDo> searchHits = elasticsearchTemplate.search(query,
        SystemLogEsDo.class);
    List<SystemLog> systemLogs = searchHits.getSearchHits().stream()
        .map(SearchHit::getContent).map(res -> SystemLogConvertor.toEntity(res).orElse(null))
        .filter(
            Objects::nonNull)
        .peek(systemLogDomain -> systemLogDomain.setRecordTime(
            CommonUtil.convertToAccountZone(systemLogDomain.getRecordTime(),
                ZoneOffset.UTC)))
        .toList();
    return new PageImpl<>(systemLogs, pageRequest, searchHits.getTotalHits());

  }
}
